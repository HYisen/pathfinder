package net.alexhyisen.pathfinder.gui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class ProgressManager {
    private Label label;
    private ProgressBar progressBar;
    private ToggleButton toggleButton;

    private ScheduledExecutorService ses;
    private ExecutorService es;

    private ScheduledFuture sf;
    private CompletableFuture<Boolean> cf;

    private AtomicInteger num;
    private int max;

    private Callable<Boolean> task;

    ProgressManager(Label label, ProgressBar progressBar, ToggleButton toggleButton,
                    ScheduledExecutorService ses, ExecutorService es, int max, Callable<Boolean> task) {
        this.label = label;
        this.progressBar = progressBar;
        this.toggleButton = toggleButton;
        this.ses = ses;
        this.es = es;
        this.max = max;
        this.task = task;

        init();
    }

    private void init() {
        num = new AtomicInteger(0);

        toggleButton.setOnAction(event -> {
            if (toggleButton.isSelected()) {
                num.set(0);
                cf = new CompletableFuture<>();
                System.out.println("play");
                sf = ses.scheduleAtFixedRate(() -> {
                    Platform.runLater(this::updateProgress);

                    int current = num.addAndGet(1);
                    System.out.println("num " + current);

                    if (current > max || current <= 0) {
                        Platform.runLater(() -> toggleButton.fire());
                    }
                }, 1000, 500, TimeUnit.MILLISECONDS);

                es.submit(() -> {
                    try {
                        cf.complete(task.call());
                    } catch (Exception e) {
                        e.printStackTrace();
                        cf.completeExceptionally(e);
                    }
                });
                cf.thenAccept(v -> num.set(v ? -2 : max - 1));
            } else {
                System.out.println("stop");
                sf.cancel(false);
                cf.cancel(true);
                System.out.println("final status = " + num.get());
                label.setText(num.get() >= max ? "completed" : "failed");
            }
        });

        Platform.runLater(this::updateProgress);
    }

    private void updateProgress() {
        int current = num.get();
        label.setText(String.format("%5d/%5d", current, max));
        progressBar.setProgress((double) current / max);
    }
}
