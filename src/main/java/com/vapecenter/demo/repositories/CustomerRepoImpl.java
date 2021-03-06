package com.vapecenter.demo.repositories;

import com.vapecenter.demo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class CustomerRepoImpl implements CustomerRepo {

    @Autowired
    JdbcTemplate template;

    Logger log = Logger.getLogger(CustomerRepoImpl.class.getName());

    @Override
    public Users getUser(int id) {
        String sql = "SELECT * FROM VapeCenter.Users WHERE userId="+id;

        return this.template.query(sql, new ResultSetExtractor<Users>() {
            @Override
            public Users extractData(ResultSet rs) throws SQLException, DataAccessException {

                Users user = new Users();

                int userId, userType, zipcode, phone;
                String firstName, lastName, streetName, city, email, password;

                while (rs.next()) {
                    userId = rs.getInt("userId");
                    userType = rs.getInt("userType");
                    zipcode = rs.getInt("zipcode");
                    phone = rs.getInt("phone");

                    firstName = rs.getString("firstName");
                    lastName = rs.getString("lastNAme");
                    streetName = rs.getString("streetName");
                    city = rs.getString("city");
                    email = rs.getString("email");
                    password = rs.getString("password");

                    user.setUserId(userId);
                    user.setUserType(userType);
                    user.setZipcode(zipcode);
                    user.setPhone(phone);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setStreetName(streetName);
                    user.setCity(city);
                    user.setEmail(email);
                    user.setPassword(password);
                }
                return user;
            }
        });
    }

    @Override
    public ArrayList<Products> getProducts() {
        String sql = "SELECT * FROM Products WHERE active =1";
        return this.template.query(sql, new ResultSetExtractor<ArrayList<Products>>() {
            @Override
            public ArrayList<Products> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int productId, stock, fk_categoryId;
                String name, description, pictureLink;
                double price;
                int active;
                ArrayList<Products> products = new ArrayList<>();

                while (rs.next()) {

                    productId = rs.getInt("productId");
                    stock = rs.getInt("stock");
                    fk_categoryId = rs.getInt("fk_categoryId");
                    name = rs.getString("name");
                    description = rs.getString("description");
                    pictureLink = rs.getString("pictureLink");
                    active = rs.getInt("active");
                    price = rs.getDouble("price");

                    products.add(new Products(productId, stock, fk_categoryId, name, description, pictureLink, active, price));
                }
                return products;
            }
        });

    }

    @Override
    public ArrayList<Products> getAllProducts() {
        String sql = "SELECT productId, name, description, price, pictureLink, active, stock, categoryName FROM Products\n" +
                "LEFT JOIN Category\n" +
                "ON Products.fk_categoryId = Category.categoryId";
        return this.template.query(sql, new ResultSetExtractor<ArrayList<Products>>() {
            @Override
            public ArrayList<Products> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int productId, stock;
                String name, description, pictureLink, categoryName;
                double price;
                int active;
                ArrayList<Products> products = new ArrayList<>();

                while (rs.next()) {
                    productId = rs.getInt("productId");
                    name = rs.getString("name");
                    categoryName = rs.getString("categoryName");
                    description = rs.getString("description");
                    price = rs.getDouble("price");
                    pictureLink = rs.getString("pictureLink");
                    active = rs.getInt("active");
                    stock = rs.getInt("stock");

                    products.add(new Products(productId, stock, name, description, pictureLink, categoryName, active, price));
                }
                return products;
            }
        });

    }

    @Override
    public ArrayList<Products> getProductsByCategory(int categoryId) {
        String sql = "SELECT * FROM Products WHERE active =1 AND fk_categoryId = ?";
        return this.template.query(sql, new ResultSetExtractor<ArrayList<Products>>() {
            @Override
            public ArrayList<Products> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int productId, stock, fk_categoryId;
                String name, description, pictureLink;
                double price;
                int active;
                ArrayList<Products> products = new ArrayList<>();

                while (rs.next()) {
                    productId = rs.getInt("productId");
                    name = rs.getString("name");
                    fk_categoryId = rs.getInt("fk_categoryId");
                    description = rs.getString("description");
                    price = rs.getDouble("price");
                    pictureLink = rs.getString("pictureLink");
                    active = rs.getInt("active");
                    stock = rs.getInt("stock");

                    products.add(new Products(productId, stock, fk_categoryId, name, description, pictureLink, active, price));
                }
                return products;
            }
        }, categoryId);

    }

    @Override
    public ArrayList<Products> searchProduct(String search) {
        search = "%" + search + "%";
        String sql = "SELECT productId, name, description, price, pictureLink, active, stock, fk_categoryId FROM Products\n" +
                "INNER JOIN Category\n" +
                "ON Products.fk_categoryId = Category.categoryId\n" +
                "WHERE active = 1 AND (name LIKE ? OR categoryName LIKE ?)";
        return this.template.query(sql, new ResultSetExtractor<ArrayList<Products>>() {
            @Override
            public ArrayList<Products> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int productId, stock, fk_categoryId;
                String name, description, pictureLink;
                double price;
                int active;
                ArrayList<Products> products = new ArrayList<>();

                while (rs.next()) {
                    productId = rs.getInt("productId");
                    name = rs.getString("name");
                    fk_categoryId = rs.getInt("fk_categoryId");
                    description = rs.getString("description");
                    price = rs.getDouble("price");
                    pictureLink = rs.getString("pictureLink");
                    active = rs.getInt("active");
                    stock = rs.getInt("stock");

                    products.add(new Products(productId, stock, fk_categoryId, name, description, pictureLink, active, price));
                }
                log.info("repo searchProduct result: " + products.size());
                return products;
            }
        }, search, search);

    }

    @Override
    public Products getProductById(int productId) {
        String sql = "SELECT * FROM VapeCenter.Products WHERE active = 1 AND productId = ?";
        RowMapper<Products> rowMapper = new BeanPropertyRowMapper<>(Products.class);

        Products product = template.queryForObject(sql, rowMapper, productId);


        return product;
    }

    @Override
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM Category WHERE categoryId = ?";
        RowMapper<Category> rowMapper = new BeanPropertyRowMapper<>(Category.class);

        Category category = template.queryForObject(sql, rowMapper, categoryId);

        return category;
    }

    @Override
    public ShipingMethod getShippingMethodById(int shippingId) {
        String sql = "SELECT * FROM shippingMethod WHERE shippingId = ?";
        RowMapper<ShipingMethod> rowMapper = new BeanPropertyRowMapper<>(ShipingMethod.class);

        ShipingMethod shipingMethod = template.queryForObject(sql, rowMapper, shippingId);

        return shipingMethod;
    }

    @Override
    public List<ShipingMethod> getShippingMethods() {
        String sql = "SELECT * FROM shippingMethod";
        return this.template.query(sql, new ResultSetExtractor<ArrayList<ShipingMethod>>() {
            @Override
            public ArrayList<ShipingMethod> extractData(ResultSet rs) throws SQLException, DataAccessException {
                int shippingId;
                String companyName;
                double price;
                List<ShipingMethod> shippingMethods = new ArrayList<>();

                while (rs.next()) {
                    shippingId = rs.getInt("shippingId");
                    companyName = rs.getString("companyName");
                    price = rs.getDouble("price");

                    shippingMethods.add(new ShipingMethod(shippingId, companyName, price));
                }
                return (ArrayList<ShipingMethod>) shippingMethods;
            }
        });
    }

    public AboutUs getAboutInfo(int aboutUsId) {
        String sql = "SELECT * FROM VapeCenter.AboutUs WHERE aboutUsId = ?";
        RowMapper<AboutUs> rowMapper = new BeanPropertyRowMapper<>(AboutUs.class);

        AboutUs aboutUs = template.queryForObject(sql, rowMapper, aboutUsId);

        return aboutUs;
    }

    @Override
    public Products addProduct(Products product) {
        String sql = "INSERT INTO VapeCenter.Products VALUES(default,?,?,?,?,?,?,?)";
        String name = product.getName();
        String description = product.getDescription();
        double price = product.getPrice();
        String pictureLink = product.getPictureLink();
        int active = product.getActive();
        int stock = product.getStock();
        int categoryId = product.getFk_categoryId();

        this.template.update(sql, name, description, price, pictureLink, active, stock, categoryId);

        return product;
    }

    @Override
    public boolean createOrder(List<Cart> cart, Checkout checkout, ShipingMethod shipingMethod, double totalPrice){
        //create new order

        String sql = "INSERT INTO Orders VALUE (DEFAULT, NULL, ?, NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
        String firstName = checkout.getFirstName();
        String lastName = checkout.getLastName();
        int phone = checkout.getPhone();
        String email = checkout.getEmail();
        String streetName = checkout.getStreet();
        int zipcode = checkout.getZipcode();
        String city = checkout.getCity();

        int shippingId = shipingMethod.getShippingId();

        this.template.update(sql, shippingId, totalPrice, firstName, lastName, phone, email, streetName, zipcode, city);

        //get order id from created order

        sql = "SELECT MAX(orderId) AS orderId FROM Orders";
        RowMapper<Orders> rowMapper = new BeanPropertyRowMapper<>(Orders.class);

        Orders orders = template.queryForObject(sql, rowMapper);

        int orderId = orders.getOrderId();
        log.info("orderId = " + orderId);

        //add products to order

        List<Products> products = getProducts();

        for(Cart c : cart){
            sql = "INSERT INTO OrderedProducts VALUE (DEFAULT, ?, ?, ?, ?, ?)";
            int productId = c.getProductId();
            int productAmount = c.getAmount();
            String productName = "";
            double productPrice = 0;

            for(Products p : products) {
                if(c.getProductId() == p.getProductId()) {
                    productName = p.getName();
                    productPrice = p.getPrice();
                }
            }

            this.template.update(sql, productId, orderId, productName, productAmount, productPrice);
        }

        return true;
    }

    public ArrayList<Category> getAllCategories() {
            String sql = "SELECT categoryId, categoryName, categoryDescription, categorySubList FROM Category";
            return this.template.query(sql, new ResultSetExtractor<ArrayList<Category>>() {
                @Override
                public ArrayList<Category> extractData(ResultSet rs) throws SQLException, DataAccessException {
                    int categoryId, categorySubList;
                    String categoryName, categoryDescription;
                    ArrayList<Category> subCategories = new ArrayList<>();

                    while (rs.next()) {
                        categoryId = rs.getInt("categoryId");
                        categoryName = rs.getString("categoryName");
                        categoryDescription = rs.getString("categoryDescription");
                        categorySubList = rs.getInt("categorySubList");

                        subCategories.add(new Category(categoryId, categoryName, categoryDescription, categorySubList));
                    }
                    return subCategories;
                }
            });

    }

    @Override
    public Products updateProducts(Products products) {
        log.info(""+products.toString());
        String sql = "UPDATE VapeCenter.Products SET name = ?, description = ?, price = ?, pictureLink = ? ,active = ?,stock = ? WHERE productId = ?";
        String name = products.getName();
        String description = products.getDescription();
        double price = products.getPrice();
        String pictureLink = products.getPictureLink();
        int active = products.getActive();
        int stock = products.getStock();
        int productId = products.getProductId();

        this.template.update(sql, name, description, price, pictureLink, active, stock, productId);
        return products;
    }

    @Override
    public Products findProduct(int productId) {
        String sql = "SELECT * FROM VapeCenter.Products WHERE productId = ?";
        log.info("id from producs :" + productId);

        RowMapper<Products> rowMapper = new BeanPropertyRowMapper<>(Products.class);

        Products products = template.queryForObject(sql, rowMapper, productId);

        return products;
    }
        @Override
        public void updateStock(Integer productId, Integer stock) {
        String sql = "UPDATE VapeCenter.Products SET stock = ? WHERE productId=?";

        this.template.update(sql, stock, productId);
    }

    @Override
    public void removeProduct(int id) {

        String sql = "DELETE FROM VapeCenter.Products WHERE productId=?";
        this.template.update(sql, id);
    }
}




