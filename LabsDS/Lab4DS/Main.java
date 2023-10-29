package org.example;

import java.util.*;


class ReadWriteLock {
    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;

    public synchronized void lockRead() throws InterruptedException {
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }

    public synchronized void unlockRead() {
        readers--;
        notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {
        writeRequests++;
        while (readers > 0 || writers > 0) {
            wait();
        }
        writeRequests--;
        writers++;
    }

    public synchronized void unlockWrite() {
        writers--;
        notifyAll();
    }
}

class Database {
    private final Map<String, String> data = new HashMap<>();
    private final ReadWriteLock lock = new ReadWriteLock();

    public void addOrUpdate(String name, String phone) throws InterruptedException {
        lock.lockWrite();
        try {
            System.out.println("Додаємо/оновлюємо запис: " + name + " - " + phone);
            data.put(name, phone);
        } finally {
            lock.unlockWrite();
        }
    }

    public String findPhoneByName(String name) throws InterruptedException {
        lock.lockRead();
        try {
            System.out.println("Шукаємо телефон за іменем: " + name);
            return data.get(name);
        } finally {
            lock.unlockRead();
        }
    }

    public String findNameByPhone(String phone) throws InterruptedException {
        lock.lockRead();
        try {
            System.out.println("Шукаємо ім'я за телефоном: " + phone);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (Objects.equals(entry.getValue(), phone)) {
                    return entry.getKey();
                }
            }
            return null;
        } finally {
            lock.unlockRead();
        }
    }

    public void deleteByName(String name) throws InterruptedException {
        lock.lockWrite();
        try {
            System.out.println("Видаляємо запис за іменем: " + name);
            data.remove(name);
        } finally {
            lock.unlockWrite();
        }
    }
}

class ReaderByName extends Thread {
    private final Database db;
    private final String name;

    public ReaderByName(Database db, String name) {
        this.db = db;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + ": " + db.findPhoneByName(name));
        } catch (InterruptedException ignored) {}
    }
}

class ReaderByPhone extends Thread {
    private final Database db;
    private final String phone;

    public ReaderByPhone(Database db, String phone) {
        this.db = db;
        this.phone = phone;
    }

    @Override
    public void run() {
        try {
            System.out.println(phone + ": " + db.findNameByPhone(phone));
        } catch (InterruptedException ignored) {}
    }
}

class Writer extends Thread {
    private final Database db;
    private final String name;
    private final String phone;

    public Writer(Database db, String name, String phone) {
        this.db = db;
        this.name = name;
        this.phone = phone;
    }

    @Override
    public void run() {
        try {
            if (phone == null) {
                db.deleteByName(name);
            } else {
                db.addOrUpdate(name, phone);
            }
        } catch (InterruptedException ignored) {}
    }
}

public class Main {
    public static void main(String[] args) {
        Database db = new Database();

        Thread writer1 = new Writer(db, "Іванов І.І.", "1234567890");
        Thread writer2 = new Writer(db, "Петров П.П.", "0987654321");
        Thread reader1 = new ReaderByName(db, "Іванов І.І.");
        Thread reader2 = new ReaderByPhone(db, "0987654321");

        writer1.start();
        writer2.start();
        reader1.start();
        reader2.start();
    }
}
