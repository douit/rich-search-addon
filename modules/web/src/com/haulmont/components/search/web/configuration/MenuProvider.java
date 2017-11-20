package com.haulmont.components.search.web.configuration;

import com.haulmont.cuba.gui.components.mainwindow.AppMenu;
import com.haulmont.cuba.web.App;
import org.springframework.context.annotation.*;

@Configuration
public class MenuProvider {

    @Lazy
    @Bean("search_AppMenu")
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
    public AppMenu applicationMenu() {
        return ((AppMenu)App.getInstance().getTopLevelWindow().getComponentNN("mainMenu"));
    }
}