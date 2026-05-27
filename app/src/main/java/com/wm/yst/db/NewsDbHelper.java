package com.wm.yst.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.wm.yst.model.Collect;
import com.wm.yst.model.NewsItem;
import com.wm.yst.model.User;
import com.wm.yst.util.PasswordUtils;
import com.wm.yst.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class NewsDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sulan_news.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USER = "user";
    public static final String TABLE_COLLECT = "collect";

    public NewsDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "salt TEXT NOT NULL, " +
                "create_time TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_COLLECT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "uniquekey TEXT NOT NULL, " +
                "news_title TEXT NOT NULL, " +
                "news_url TEXT, " +
                "news_source TEXT, " +
                "news_date TEXT, " +
                "category TEXT, " +
                "thumbnail_url TEXT, " +
                "collect_time TEXT NOT NULL, " +
                "UNIQUE(username, uniquekey)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public boolean registerUser(String username, String password) {
        if (isBlank(username) || isBlank(password) || isUserExists(username)) {
            return false;
        }
        String salt = PasswordUtils.generateSalt();
        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("password_hash", PasswordUtils.hashPassword(password, salt));
        values.put("salt", salt);
        values.put("create_time", TimeUtils.now());
        return getWritableDatabase().insert(TABLE_USER, null, values) != -1;
    }

    public boolean isUserExists(String username) {
        if (isBlank(username)) {
            return false;
        }
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_USER,
                new String[]{"id"},
                "username = ?",
                new String[]{username.trim()},
                null,
                null,
                null
        )) {
            return cursor.moveToFirst();
        }
    }

    @Nullable
    public User getUser(String username) {
        if (isBlank(username)) {
            return null;
        }
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_USER,
                null,
                "username = ?",
                new String[]{username.trim()},
                null,
                null,
                null
        )) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            return readUser(cursor);
        }
    }

    public boolean validateLogin(String username, String password) {
        User user = getUser(username);
        return user != null && PasswordUtils.verifyPassword(password, user.getSalt(), user.getPasswordHash());
    }

    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        if (isBlank(newPassword) || !validateLogin(username, oldPassword)) {
            return false;
        }
        String newSalt = PasswordUtils.generateSalt();
        ContentValues values = new ContentValues();
        values.put("salt", newSalt);
        values.put("password_hash", PasswordUtils.hashPassword(newPassword, newSalt));
        return getWritableDatabase().update(
                TABLE_USER,
                values,
                "username = ?",
                new String[]{username.trim()}
        ) > 0;
    }

    public boolean addCollect(String username, NewsItem newsItem) {
        if (isBlank(username) || newsItem == null || isBlank(newsItem.getUniquekey()) || isBlank(newsItem.getTitle())) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put("username", username.trim());
        values.put("uniquekey", newsItem.getUniquekey());
        values.put("news_title", newsItem.getTitle());
        values.put("news_url", newsItem.getUrl());
        values.put("news_source", newsItem.getAuthorName());
        values.put("news_date", newsItem.getDate());
        values.put("category", newsItem.getCategory());
        values.put("thumbnail_url", newsItem.getThumbnailPicS());
        values.put("collect_time", TimeUtils.now());
        return getWritableDatabase().insertWithOnConflict(
                TABLE_COLLECT,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        ) != -1;
    }

    public boolean removeCollect(String username, String uniquekey) {
        if (isBlank(username) || isBlank(uniquekey)) {
            return false;
        }
        return getWritableDatabase().delete(
                TABLE_COLLECT,
                "username = ? AND uniquekey = ?",
                new String[]{username.trim(), uniquekey}
        ) > 0;
    }

    public boolean isCollected(String username, String uniquekey) {
        if (isBlank(username) || isBlank(uniquekey)) {
            return false;
        }
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_COLLECT,
                new String[]{"id"},
                "username = ? AND uniquekey = ?",
                new String[]{username.trim(), uniquekey},
                null,
                null,
                null
        )) {
            return cursor.moveToFirst();
        }
    }

    public List<Collect> getCollectList(String username) {
        List<Collect> collectList = new ArrayList<>();
        if (isBlank(username)) {
            return collectList;
        }
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_COLLECT,
                null,
                "username = ?",
                new String[]{username.trim()},
                null,
                null,
                "id DESC"
        )) {
            while (cursor.moveToNext()) {
                collectList.add(readCollect(cursor));
            }
        }
        return collectList;
    }

    private User readUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
        user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow("password_hash")));
        user.setSalt(cursor.getString(cursor.getColumnIndexOrThrow("salt")));
        user.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("create_time")));
        return user;
    }

    private Collect readCollect(Cursor cursor) {
        Collect collect = new Collect();
        collect.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        collect.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
        collect.setUniquekey(cursor.getString(cursor.getColumnIndexOrThrow("uniquekey")));
        collect.setNewsTitle(cursor.getString(cursor.getColumnIndexOrThrow("news_title")));
        collect.setNewsUrl(cursor.getString(cursor.getColumnIndexOrThrow("news_url")));
        collect.setNewsSource(cursor.getString(cursor.getColumnIndexOrThrow("news_source")));
        collect.setNewsDate(cursor.getString(cursor.getColumnIndexOrThrow("news_date")));
        collect.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
        collect.setThumbnailUrl(cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_url")));
        collect.setCollectTime(cursor.getString(cursor.getColumnIndexOrThrow("collect_time")));
        return collect;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
