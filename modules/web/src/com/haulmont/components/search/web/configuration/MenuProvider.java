package com.haulmont.components.search.web.configuration;

import com.haulmont.cuba.gui.components.mainwindow.AppMenu;
import com.haulmont.cuba.web.App;
import org.springframework.context.annotation.*;

/**
 * Provides application menu {@link AppMenu} injection
 *
 * @see com.haulmont.components.search.strategy.MainMenuSearchStrategy
 */
@Configuration
public class MenuProvider {

    @Bean("search_AppMenu")
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
    public AppMenu applicationMenu() {
        return ((AppMenu)App.getInstance().getTopLevelWindow().getComponentNN("mainMenu"));
    }
}
