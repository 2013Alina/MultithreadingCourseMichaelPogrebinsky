package com.multi.MultithreadingCourseMichaelPogrebinsky;

public class MinMaxMetrics {
  
    private volatile long min;
    private volatile long max;

    public MinMaxMetrics() {
        this.max = Long.MIN_VALUE;
        this.min = Long.MAX_VALUE;
    }

    public void addSample(long newSample) {
        synchronized (this) {
            if (newSample < min) {
                min = newSample;
            }
            if (newSample > max) {
                max = newSample;
            }
        }
    }

    public long getMin() {
        return this.min;
    }

    public long getMax() {
        return this.max;
    }

    public static void main(String[] args) {
        MinMaxMetrics metrics = new MinMaxMetrics();

        metrics.addSample(50);
        metrics.addSample(75);
        metrics.addSample(120);
        metrics.addSample(25);
        metrics.addSample(220);
        metrics.addSample(115);

        System.out.println("Минимальное значение: " + metrics.getMin());
        System.out.println("Максимальное значение: " + metrics.getMax());
    }
    // MinMaxMetrics — это аналитический класс, используемый для отслеживания минимума и максимума определенного бизнес-показателя или показателя производительности в нашем приложении.
    // Приложение для торговли акциями, которое ежедневно отслеживает минимальную и максимальную цену акций.
    // Каждый метод может вызываться одновременно любым заданным количеством потоков, поэтому класс должен быть потокобезопасным.
    // Кроме того, этот класс используется для аналитики, поэтому он должен быть максимально производительным, поскольку мы не хотим, чтобы он слишком сильно замедлял потоки нашей бизнес-логики.
    // Потоки, вызывающие getMin() или getMax(), интересуются только одним из значений и никогда не интересуются одновременно минимальным и максимальным значениями.
}
