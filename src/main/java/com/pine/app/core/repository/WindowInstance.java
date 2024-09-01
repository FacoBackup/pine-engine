package com.pine.app.core.repository;

import com.pine.app.core.window.AbstractWindow;

public record WindowInstance(Thread thread, AbstractWindow window) {
}
