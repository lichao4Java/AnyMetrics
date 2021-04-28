package org.anymetrics.console.base;

import java.io.Serializable;
import java.util.List;

public class ResultPage<T> implements Serializable {
    private static final long serialVersionUID = 5472321653620726832L;
    private static final int DEFAULT_NAVIGATOR_SIZE = 10;
    private int currentPage;
    private int pageSize;
    private int pageCount;
    private int totalCount;
    private boolean havaNextPage;
    private boolean havePrePage;
    private int navigatorSize;
    private List<T> items;

    public ResultPage() {
        this.currentPage = 1;
        this.pageSize = 20;
        this.pageCount = 1;
    }

    public ResultPage(int totalCount, int pageSize, int currentPage) {
        this(totalCount, pageSize, currentPage, 10);
    }

    public ResultPage(int totalCount, int pageSize, int currentPage, int navigatorSize) {
        this.currentPage = 1;
        this.pageSize = 20;
        this.pageCount = 1;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.pageCount = this.operatorPageCount();
        this.navigatorSize = navigatorSize;
    }

    public ResultPage(int totalCount, int pageSize, int currentPage, List<T> items) {
        this.currentPage = 1;
        this.pageSize = 20;
        this.pageCount = 1;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.pageCount = this.operatorPageCount();
        this.items = items;
    }

    public int operatorPageCount() {
        int pageCount = 0;
        if (this.pageSize != 0) {
            pageCount = this.totalCount / this.pageSize;
            if (this.totalCount % this.pageSize != 0) {
                ++pageCount;
            }
        }

        return pageCount;
    }

    public int getCurrentPage() {
        this.currentPage = this.currentPage < this.pageCount ? this.currentPage : this.pageCount;
        this.currentPage = this.currentPage < 1 ? 1 : this.currentPage;
        return this.currentPage;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public boolean isHaveNextPage() {
        this.havaNextPage = false;
        if (this.pageCount > 1 && this.pageCount > this.getCurrentPage()) {
            this.havaNextPage = true;
        }

        return this.havaNextPage;
    }

    public boolean isHavePrePage() {
        this.havePrePage = false;
        if (this.pageCount > 1 && this.currentPage > 1) {
            this.havePrePage = true;
        }

        return this.havePrePage;
    }

    private int getNavigatorIndex(boolean isBegin) {
        int beginNavigatorIndex = this.getCurrentPage() - this.navigatorSize / 2;
        int endNavigatorIndex = this.getCurrentPage() + this.navigatorSize / 2;
        beginNavigatorIndex = beginNavigatorIndex < 1 ? 1 : beginNavigatorIndex;
        endNavigatorIndex = endNavigatorIndex < this.pageCount ? endNavigatorIndex : this.pageCount;

        while(endNavigatorIndex - beginNavigatorIndex < this.navigatorSize && (beginNavigatorIndex != 1 || endNavigatorIndex != this.pageCount)) {
            if (beginNavigatorIndex > 1) {
                --beginNavigatorIndex;
            } else if (endNavigatorIndex < this.pageCount) {
                ++endNavigatorIndex;
            }
        }

        return isBegin ? beginNavigatorIndex : endNavigatorIndex;
    }

    public int getBeginNavigatorIndex() {
        return this.getNavigatorIndex(true);
    }

    public int getEndNavigatorIndex() {
        return this.getNavigatorIndex(false);
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
