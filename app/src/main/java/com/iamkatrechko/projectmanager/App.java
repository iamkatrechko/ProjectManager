package com.iamkatrechko.projectmanager;

import android.app.Application;

import ru.yandex.speechkit.SpeechKit;

/**
 * Главная точка входа в приложение
 * @author iamkatrechko
 *         Date: 15.06.2017
 */
public class App extends Application {

    /** Ключ для использования голосового ввода */
    private static final String SPEECH_KIT_KEY = "79b8f85e-b240-44a0-a2ff-a2f618831303";

    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация менеджера задач
        ProjectLab.get(this);
        // Инициализация тем
        new Themes(this);
        // Инициализация голосового ввода
        SpeechKit.getInstance().configure(getApplicationContext(), SPEECH_KIT_KEY);
    }
}
