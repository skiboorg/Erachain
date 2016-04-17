package core.item;

//import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
// import org.apache.log4j.Logger;

import org.json.simple.JSONObject;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
//import com.google.common.primitives.Longs;

import controller.Controller;
import core.account.Account;
import core.crypto.Base58;
import core.transaction.Transaction;
import database.DBSet;
//import database.DBMap;
import database.Item_Map;
import database.wallet.FavoriteItem;
import database.IssueItemMap;

public abstract class ItemCls {

	public static final int ASSET_TYPE = 1;
	public static final int NOTE_TYPE = 2;
	public static final int PERSON_TYPE = 3;
	public static final int STATUS_TYPE = 4;
	public static final int UNION_TYPE = 5;

	protected static final int TYPE_LENGTH = 2;
	protected static final int CREATOR_LENGTH = Account.ADDRESS_LENGTH;
	protected static final int NAME_SIZE_LENGTH = 1;
	public static final int MAX_NAME_LENGTH = 255 * NAME_SIZE_LENGTH;
	protected static final int DESCRIPTION_SIZE_LENGTH = 4;
	protected static final int REFERENCE_LENGTH = Transaction.SIGNATURE_LENGTH;
	protected static final int BASE_LENGTH = TYPE_LENGTH + CREATOR_LENGTH + NAME_SIZE_LENGTH + DESCRIPTION_SIZE_LENGTH;
	
	protected static final int TIMESTAMP_LENGTH = Transaction.TIMESTAMP_LENGTH;

	//protected DBMap dbMap;
	//protected DBMap dbIssueMap;
	
	protected String TYPE_NAME = "unknown";
	protected byte[] typeBytes;
	protected Account creator;
	protected String name;
	protected String description;
	protected long key = -1;
	protected byte[] reference = null; // this is signature of issued record
	
	public ItemCls(byte[] typeBytes, Account creator, String name, String description)
	{
		this.typeBytes = typeBytes;
		this.creator = creator;
		this.name = name;
		this.description = description;
		
	}
	public ItemCls(int type, Account creator, String name, String description)
	{
		this(new byte[TYPE_LENGTH], creator, name, description);
		this.typeBytes[0] = (byte)type;
	}

	//GETTERS/SETTERS

	public abstract String getItemType();
	public abstract String getItemSubType();

	public abstract Item_Map getDBMap(DBSet db);
	public abstract IssueItemMap getDBIssueMap(DBSet db);


	public byte[] getType()
	{
		return this.typeBytes;
	}

	public Account getCreator() {
		return this.creator;
	}
	
	public String getName() {
		return this.name;
	}
	public long getKey() {
		return this.getKey(DBSet.getInstance());
	}
	public long getKey(DBSet db) {
		// TODO if ophran ?
		if (this.key <0) this.key = this.getDBIssueMap(db).get(this.reference);
		return this.key;
	}
	
	public String getDescription() {
		return this.description;
	}
		
	public byte[] getReference() {
		return this.reference;
	}
	public void setReference(byte[] reference) {
		this.reference = reference;
	}
		
	public boolean isConfirmed() {
		return isConfirmed(DBSet.getInstance());
	}
	
	public boolean isConfirmed(DBSet db) {
		return this.getDBIssueMap(db).contains(this.reference);
	}	
	
	public byte[] toBytes(boolean includeReference)
	{

		byte[] data = new byte[0];
		
		//WRITE TYPE
		data = Bytes.concat(data, this.typeBytes);

		//WRITE CREATOR
		try
		{
			data = Bytes.concat(data, Base58.decode(this.creator.getAddress()));
		}
		catch(Exception e)
		{
			//DECODE EXCEPTION
		}
		
		//WRITE NAME SIZE
		byte[] nameBytes = this.name.getBytes(StandardCharsets.UTF_8);
		data = Bytes.concat(data, new byte[]{(byte)nameBytes.length});
		
		//WRITE NAME
		data = Bytes.concat(data, nameBytes);
		
		//WRITE DESCRIPTION SIZE
		byte[] descriptionBytes = this.description.getBytes(StandardCharsets.UTF_8);
		int descriptionLength = descriptionBytes.length;
		byte[] descriptionLengthBytes = Ints.toByteArray(descriptionLength);
		data = Bytes.concat(data, descriptionLengthBytes);
				
		//WRITE DESCRIPTION
		data = Bytes.concat(data, descriptionBytes);
		
		if(includeReference)
		{
			//WRITE REFERENCE
			data = Bytes.concat(data, this.reference);
		}
		else
		{
			//WRITE EMPTY REFERENCE
			// data = Bytes.concat(data, new byte[64]);
		}
		
		return data;
	}

	public int getDataLength(boolean includeReference) 
	{
		return BASE_LENGTH
				+ this.name.getBytes(StandardCharsets.UTF_8).length
				+ this.description.getBytes(StandardCharsets.UTF_8).length
				+ (includeReference? REFERENCE_LENGTH: 0);
	}	
	
	//OTHER
	
	public String toString()
	{		
		return "(" + this.key + ":" + this.typeBytes.toString() + ") " + this.name;
	}
	
	public String getShort()
	{
		return "(" + this.key + ":" + this.typeBytes.toString() + ") " + this.name.substring(0, Math.min(this.name.length(), 4));
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		
		JSONObject itemJSON = new JSONObject();

		// ADD DATA
		itemJSON.put("item_type", this.getItemType());
		itemJSON.put("item_type_sub", this.getItemSubType());
		itemJSON.put("type0", Byte.toUnsignedInt(this.typeBytes[0]));
		itemJSON.put("type1", Byte.toUnsignedInt(this.typeBytes[1]));
		itemJSON.put("key", this.key);
		itemJSON.put("name", this.name);
		itemJSON.put("description", this.description);
		itemJSON.put("creator", this.creator.getAddress());
		itemJSON.put("isConfirmed", this.isConfirmed());
		itemJSON.put("reference", Base58.encode(this.reference));
		
		Transaction txReference = Controller.getInstance().getTransaction(this.reference);
		if(txReference != null)
		{
			itemJSON.put("timestamp", txReference.getTimestamp());
		}
		
		return itemJSON;
	}

	public long insertToMap(DBSet db)
	{
		//INSERT INTO DATABASE
		Item_Map dbMap = this.getDBMap(db);
		int mapSize = dbMap.size();
		//LOGGER.info("GENESIS MAP SIZE: " + assetMap.size());
		long key = 0l;
		if (mapSize == 0) {
			// initial map set
			dbMap.set(0l, this);
		} else {
			key = dbMap.add(this);
			//this.asset.setKey(key);
		}
		
		//SET ORPHAN DATA
		this.getDBIssueMap(db).set(this.reference, key);
		
		return key;
		
	}
	
	public long removeFromMap(DBSet db)
	{
		//DELETE FROM DATABASE
		IssueItemMap issueDB = this.getDBIssueMap(db);
		long key = issueDB.get(this.reference);
		this.getDBMap(db).delete(key);	
				
		//DELETE ORPHAN DATA
		issueDB.delete(this.reference);
		
		return key;

	}

}
