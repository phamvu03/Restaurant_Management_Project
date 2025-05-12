package com.restaurant.restaurant_management_project.dao;





import com.restaurant.restaurant_management_project.model.RMenuItem;

import java.util.List;

public interface MenuItemDao {
    public List<RMenuItem> getAll();
    public List<RMenuItem> getAll(int offset, int limit);
    public boolean add(RMenuItem item);
    public boolean update(RMenuItem item);
    public boolean delete(RMenuItem item);

}
