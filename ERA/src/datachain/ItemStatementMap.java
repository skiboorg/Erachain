package datachain;

import java.util.HashMap;
import java.util.Map;

import org.mapdb.Atomic;
import org.mapdb.DB;

import core.item.ItemCls;
import core.item.statuses.StatusCls;
import utils.ObserverMessage;
import database.serializer.ItemSerializer;
//import database.serializer.StatusSerializer;
import datachain.DCSet;

public class ItemStatementMap extends Item_Map 
{
	
	static final String NAME = "item_statements";
	static final int TYPE = ItemCls.STATEMENT_TYPE;

	public ItemStatementMap(DCSet databaseSet, DB database)
	{
		super(databaseSet, database,
				//TYPE,
				NAME,
				ObserverMessage.RESET_STATEMENT_TYPE,
				ObserverMessage.ADD_STATEMENT_TYPE,
				ObserverMessage.REMOVE_STATEMENT_TYPE,
				ObserverMessage.LIST_STATEMENT_TYPE
				);
		}

	public ItemStatementMap(ItemStatementMap parent) 
	{
		super(parent);	
	}
	
	// type+name not initialized yet! - it call as Super in New
	protected Map<Long, ItemCls> getMap(DB database) 
	{
		
		//OPEN MAP
		return database.createTreeMap(NAME)
				.valueSerializer(new ItemSerializer(TYPE))
				//.valueSerializer(new StatusSerializer())
				.makeOrGet();
	}

}