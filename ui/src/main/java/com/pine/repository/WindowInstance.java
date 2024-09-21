package com.pine.repository;

import com.pine.window.AbstractWindow;

public record WindowInstance(Thread thread, AbstractWindow window) {
}
