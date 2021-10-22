package ru.netology;

public class Main {
    private static final int CUSTOMERS_COUNT = 3;
    private static final int CARS_TO_MAKE_COUNT = 10;

    private static final int TIME_TO_MAKE_CAR = 2_000;
    private static final int TIME_TO_USE_OLD_CAR = 1_000;

    private static final String CAR_BRAND = "Toyota";


    public static void main(String[] args) {
        final CarShowroom carShowroom = new CarShowroom();

        final Runnable manufacturerAction = getManufacturerAction(carShowroom);
        new Thread(manufacturerAction, "Производитель").start();

        final Runnable customerAction = getCustomerAction(carShowroom);
        for (int i = 0; i < CUSTOMERS_COUNT; i++) {
            new Thread(customerAction, "Покупатель" + (i + 1)).start();
        }

    }

    @SuppressWarnings("BusyWait")
    private static Runnable getCustomerAction(CarShowroom carShowroom) {
        return () -> {
            while (true) {
                try {
                    final boolean isCarBought = carShowroom.sellCar();
                    if (!isCarBought) {
                        return;
                    }
                    Thread.sleep(TIME_TO_USE_OLD_CAR);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static Runnable getManufacturerAction(CarShowroom carShowroom) {
        return () -> {
            for (int i = 0; i < CARS_TO_MAKE_COUNT; i++) {
                try {
                    Thread.sleep(TIME_TO_MAKE_CAR);
                    final boolean notDeliveryExpected = i == CARS_TO_MAKE_COUNT - 1;
                    carShowroom.receiveGoods(new Car(CAR_BRAND), notDeliveryExpected);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
