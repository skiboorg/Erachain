package org.erachain.database;
// 30/03 ++

import lombok.extern.slf4j.Slf4j;
import org.erachain.settings.Settings;
import org.erachain.utils.SimpleFileVisitorForRecursiveFolderDeletion;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.nio.file.Files;

@Slf4j
public class DLSet extends DBASet {

    final static int CURRENT_VERSION = 315;

    private PeerMap peerMap;
    private PairMapImpl pairMap;

    public DLSet(File dbFile, DB database, boolean withObserver, boolean dynamicGUI) {
        super(dbFile, database, withObserver, dynamicGUI);
        this.peerMap = new PeerMap(this, this.database);
        this.pairMap = new PairMapImpl(DBS_FAST, this, database);
    }

    static DB makeDB(File dbFile) {

        boolean isNew = !dbFile.exists();
        if (isNew) {
            dbFile.getParentFile().mkdirs();
        }

        DB database = DBMaker.newFileDB(dbFile)

                //// иначе кеширует блок и если в нем удалить трнзакции или еще что то выдаст тут же такой блок с пустыми полями
                ///// добавил dcSet.clearCache(); --
                ///.cacheDisable()

                // это чистит сама память если осталось 25% от кучи - так что она безопасная
                //.cacheHardRefEnable()
                //.cacheLRUEnable()
                ///.cacheSoftRefEnable()
                //.cacheWeakRefEnable()

                // количество точек в таблице которые хранятся в HashMap как в КЭШе
                .cacheSize(1 << 4)

               .checksumEnable()
               .mmapFileEnableIfSupported() // ++
                /// ICREATOR
                .commitFileSyncDisable() // ++

                // если при записи на диск блока процессор сильно нагружается - то уменьшить это
                .freeSpaceReclaimQ(7) // не нагружать процессор для поиска свободного места в базе данных

                //.compressionEnable()

                .transactionDisable()
                .make();

        if (isNew)
            DBASet.setVersion(database, CURRENT_VERSION);

        return database;

    }
    public static DLSet reCreateDB() {

        //OPEN DB
        File dbFile = new File(Settings.getInstance().getLocalDir(), "data.dat");

        DB database = null;
        try {
            database = makeDB(dbFile);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            try {
                Files.walkFileTree(dbFile.getParentFile().toPath(),
                        new SimpleFileVisitorForRecursiveFolderDeletion());
            } catch (Throwable e1) {
                logger.error(e1.getMessage(), e1);
            }
            database = makeDB(dbFile);
        }

        if (DBASet.getVersion(database) != CURRENT_VERSION) {
            database.close();
            logger.warn("New Version: " + CURRENT_VERSION + ". Try remake DLSet.");
            try {
                Files.walkFileTree(dbFile.getParentFile().toPath(),
                        new SimpleFileVisitorForRecursiveFolderDeletion());
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
            database = makeDB(dbFile);
            DBASet.setVersion(database, CURRENT_VERSION);

        }

        return new DLSet(dbFile, database, true, true);

    }

    public PeerMap getPeerMap() {
        return this.peerMap;
    }

    /**
     * Хранит пары на бирже - для статитки чтобы не пересчитывать
     * Ключ: пара - первый наименьший ключ
     * Значение - статистика
     * AssetKey (Long) + AssetKey (Long) -> Stats
     */
    public PairMapImpl getPairMap() {
        return this.pairMap;
    }

    @Override
    public void close() {

        if (this.database == null || this.database.isClosed())
            return;

        this.uses++;
        this.database.close();
        // улучшает работу финалайзера
        this.tables = null;
        this.uses--;

    }

}