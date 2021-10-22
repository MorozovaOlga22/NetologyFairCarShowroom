package ru.netology;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarShowroom {
    private final List<Car> cars = new ArrayList<>();
    private boolean notDeliveryExpected = false;

    private final Lock lock = new ReentrantLock(true);
    private final Condition carCondition = lock.newCondition();

    public void receiveGoods(Car newCar, boolean notDeliveryExpected) {
        lock.lock();
        try {
            System.out.println("Производитель " + newCar.getCarBrand() + " выпустил 1 авто");
            cars.add(newCar);
            if (notDeliveryExpected) {
                System.out.println("Машины больше производиться не будут");
                this.notDeliveryExpected = true;
                carCondition.signalAll();
            } else {
                carCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean sellCar() throws InterruptedException {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " зашел в автосалон");
            while (cars.size() < 1) {
                if (notDeliveryExpected) {
                    System.out.println(Thread.currentThread().getName() + " не смог купить машину");
                    return false;
                }
                System.out.println("Машин нет");
                carCondition.await();
            }

            cars.remove(0);
            System.out.println(Thread.currentThread().getName() + " уехал на новеньком авто");
            return true;
        } finally {
            lock.unlock();
        }
    }
}