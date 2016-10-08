package com.giot.memo.util;

import com.giot.memo.App;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.data.gen.DaoMaster;
import com.giot.memo.data.gen.DaoSession;
import com.giot.memo.data.gen.HistoryDao;

/**
 * 获取数据库操作的单例模式
 * Created by reed on 16/7/25.
 */
public class DaoUtil {

    private static final String DATABASE_NAME = "memo_db";
    private static volatile DaoSession instance;


    private static DaoSession getInstance() {
        if (instance == null) {
            synchronized (DaoUtil.class) {
                if (instance == null) {
                    DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(App.getContext(), DATABASE_NAME, null);
                    DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
                    instance = daoMaster.newSession();
                }
            }
        }
        return instance;
    }


    public static BillDao getBillDao() {
        return getInstance().getBillDao();
    }

    public static HistoryDao getHistoryDao() {
        return getInstance().getHistoryDao();
    }
}
