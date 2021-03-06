/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.search.strategy.loader;

import com.haulmont.addon.search.context.SearchContext;
import com.haulmont.addon.search.strategy.DefaultSearchEntry;
import com.haulmont.addon.search.strategy.SearchEntry;
import com.haulmont.cuba.gui.components.mainwindow.AppMenu;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides searching functions to {@link com.haulmont.addon.search.strategy.MainMenuSearchStrategy}
 */
public class MainMenuDataLoader {

    protected SearchContext session;
    protected List<DefaultSearchEntry> cached;

    public MainMenuDataLoader(SearchContext session, AppMenu appMenu) {
        this.session = session;
        cached = mapChildren(appMenu.getMenuItems()).collect(Collectors.toList());
    }

    protected Stream<DefaultSearchEntry> mapChildren(List<AppMenu.MenuItem> roots) {
        return roots.stream()
                .flatMap(root ->
                        root.getChildren().stream()
                                .map(item -> new FlatMenuItem(item, root))
                                .flatMap(this::traverse)
                                .filter(item -> ! item.isSeparator())
                                .filter(item -> item.getCommand() != null)
                                .map(item ->
                                        new DefaultSearchEntry(item.getId(), getQueryString(item), getCaption(root, item),
                                                "searchStrategy.mainMenu", item::isVisible)
                                ));
    }

    protected Stream<FlatMenuItem> traverse(FlatMenuItem root) {
        return Stream.concat(Stream.of(root), Optional.ofNullable(root.getChildren())
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> new FlatMenuItem(item, root))
                .flatMap(this::traverse));
    }

    protected String getCaption(AppMenu.MenuItem topRoot, FlatMenuItem item) {
        return topRoot.equals(item.getParent()) ?
                String.format("%s > %s", topRoot.getCaption(), item.getCaption()) :
                String.format("%s > ... > %s", topRoot.getCaption(), item.getCaption());
    }

    protected String getQueryString(FlatMenuItem item) {
        return String.format("%s %s", item.getCaption(),
                ObjectUtils.defaultIfNull(item.getDescription(), ""));
    }

    public List<SearchEntry> load(String pattern) {
        return StringUtils.isBlank(pattern) ?
                Collections.emptyList()
                : cached.stream()
                .filter(e -> e.getQueryString().contains(pattern.trim().toLowerCase()))
                .filter(DefaultSearchEntry::isActive)
                .collect(Collectors.toList());
    }

    protected static class FlatMenuItem implements AppMenu.MenuItem {

        protected AppMenu.MenuItem delegate;
        protected AppMenu.MenuItem parent;

        public FlatMenuItem(AppMenu.MenuItem delegate) {
            this.delegate = delegate;
        }

        public FlatMenuItem(AppMenu.MenuItem delegate, AppMenu.MenuItem parent) {
            this.delegate = delegate;
            this.parent = parent;
        }

        @Override
        public String getId() {
            return delegate.getId();
        }

        @Override
        public AppMenu getMenu() {
            return delegate.getMenu();
        }

        @Override
        public String getCaption() {
            return delegate.getCaption();
        }

        @Override
        public void setCaption(String caption) {
        }

        @Override
        public String getDescription() {
            return delegate.getDescription();
        }

        @Override
        public void setDescription(String description) {
        }

        @Override
        public String getIcon() {
            return delegate.getIcon();
        }

        @Override
        public void setIcon(String icon) {
        }

        @Override
        public boolean isVisible() {
            return delegate.isVisible();
        }

        @Override
        public void setVisible(boolean visible) {
        }

        @Override
        public String getStyleName() {
            return delegate.getStyleName();
        }

        @Override
        public void setStyleName(String styleName) {
        }

        @Override
        public Consumer<AppMenu.MenuItem> getCommand() {
            return delegate.getCommand();
        }

        @Override
        public void setCommand(Consumer<AppMenu.MenuItem> command) {
        }

        @Override
        public void addChildItem(AppMenu.MenuItem menuItem) {
        }

        @Override
        public void addChildItem(AppMenu.MenuItem menuItem, int index) {
        }

        @Override
        public void removeChildItem(AppMenu.MenuItem menuItem) {
        }

        @Override
        public void removeChildItem(int index) {
        }

        @Override
        public List<AppMenu.MenuItem> getChildren() {
            return delegate.getChildren();
        }

        @Override
        public boolean hasChildren() {
            return delegate.hasChildren();
        }

        @Override
        public boolean isSeparator() {
            return delegate.isSeparator();
        }

        public AppMenu.MenuItem getParent() {
            return parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            FlatMenuItem that = (FlatMenuItem) o;

            return delegate != null ? delegate.equals(that.delegate) : that.delegate == null;
        }

        @Override
        public int hashCode() {
            return delegate != null ? delegate.hashCode() : 0;
        }
    }
}
