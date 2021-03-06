package com.vapecenter.demo.controller;

import com.vapecenter.demo.models.*;
import com.vapecenter.demo.models.AboutUs;
import com.vapecenter.demo.models.Cart;
import com.vapecenter.demo.models.Products;
import com.vapecenter.demo.models.Users;
import com.vapecenter.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

@Controller
public class CustomerController {
    private ArrayList<Cart> cartList = new ArrayList<>();

    public CustomerController(){
        /*productsList.add(new Products(1, "Supervape", 5000000));
        productsList.add(new Products(2, "Crapvape", 5));
        productsList.add(new Products(3, "Bluevape", 550));
        productsList.add(new Products(4, "Ecovape", 320));
        productsList.add(new Products(5, "DSBvape", 1500));*/

        //cartList.add(new Cart(2, 3));

    }

    @Autowired
    CustomerService customerService;

    private final String CART = "cart";
    private final String CHECKOUT = "checkout";
    private final String DELIVERY = "delivery";
    private final String CATEGORY = "category";
    private final String SEARCH = "search";
    private final String SEARCHRESULT = "searchResult";
    private final String LISTPRODUCTS = "listProducts";
    private final String ADMINLISTPRODUCTS = "adminListProducts";

    Logger log = Logger.getLogger(CustomerController.class.getName());

    @GetMapping("/")
    public String index(HttpSession session, Model model){
        log.info("index called");


        if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
            //session.setAttribute("categories", categories = customerService.getAllCategories());
        }


        model.addAttribute("categories", customerService.getAllCategories());

        return "index";
    }


    @GetMapping("/cart")
    public String cart(Model model, HttpSession session){
        log.info("Cart is called...");

        if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
        }

        double total = 0;

        List<Products> productsList = customerService.getProducts();

        List<Cart> cart = (List<Cart>) session.getAttribute("cart");

        if(cart.size() > 0){
            session.setAttribute("checkoutStep", 1);
            log.info("checkoutStep = " + 1);

            model.addAttribute("readyToCheckout", true);
        } else {
            session.setAttribute("checkoutStep", 0);

            model.addAttribute("readyToCheckout", false);
        }

        log.info("" + cart.size());

        for(Cart c : cart){
            for(Products p : productsList){
                if (c.getProductId() == p.getProductId()) {
                    total = total + ( c.getAmount() * p.getPrice() );
                }
            }
        }

        model.addAttribute("carts", cart);
        model.addAttribute("products", productsList);
        model.addAttribute("total", total);
        model.addAttribute("categories", customerService.getAllCategories());

        return CART;
    }

    @RequestMapping(value = "/editCart", method = RequestMethod.POST)
    public String editCart(@RequestParam("productId")Integer productId, @RequestParam("amount")Integer amount, Model model, HttpSession session) throws Exception{
        log.info("editCart is called...");

        if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
        }

        List<Cart> cart = (List<Cart>) session.getAttribute("cart");

        for (Cart c : cart) {
            if(c.getProductId() == productId){
                c.setAmount(amount);
            }
        }

        session.removeAttribute("cart");
        session.setAttribute("cart", cart);
        model.addAttribute("categories", customerService.getAllCategories());

        return "redirect:/cart";
    }

    @RequestMapping(value = "/deleteCart", method = RequestMethod.POST)
    public String deleteCart(@RequestParam("productId")Integer productId, Model model, HttpSession session) throws Exception {
        log.info("deleteCart is called...");

        if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
        }

        List<Cart> cart = (List<Cart>) session.getAttribute("cart");

        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProductId() == productId) {
                cart.remove(i);
            }
        }

        session.removeAttribute("cart");
        session.setAttribute("cart", cart);
        model.addAttribute("categories", customerService.getAllCategories());

        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session){
        log.info("Checkout is called...");

        if(session.getAttribute("checkoutStep") == null){
            session.setAttribute("checkoutStep", 0);
        }

        int checkoutStep = (int) session.getAttribute("checkoutStep");

        log.info("checkoutStep = " + checkoutStep);

        model.addAttribute("categories", customerService.getAllCategories());

        if(checkoutStep > 0) {
            model.addAttribute("checkout", new Checkout());
            return CHECKOUT;
        } else {
            return "redirect:/cart";
        }
    }

    @PostMapping("/checkout")
    public String checkoutDone(@ModelAttribute Checkout checkout, Model model, HttpSession session){
        log.info("" + checkout.toString());

        session.setAttribute("checkout", checkout);
        Checkout sessionCheckout = (Checkout) session.getAttribute("checkout");

        session.setAttribute("checkoutStep", 2);

        log.info("Session: " + sessionCheckout.toString());
        model.addAttribute("categories", customerService.getAllCategories());
        return "redirect:/delivery";
    }

    @GetMapping("/delivery")
    public String delivery(Model model, HttpSession session){
        log.info("Checkout is called...");

        if(session.getAttribute("checkoutStep") == null){
            session.setAttribute("checkoutStep", 0);
        }

        int checkoutStep = (int) session.getAttribute("checkoutStep");
        log.info("checkoutStep = " + checkoutStep);

        model.addAttribute("delivery", new ShipingMethod());
        model.addAttribute("shippingMethods", customerService.getShippingMethods());
        model.addAttribute("categories", customerService.getAllCategories());
        if(checkoutStep > 1) {
            return DELIVERY;
        } else {
            return "redirect:/checkout";
        }
    }

    @PostMapping("/delivery")
    public String deliveryDone(@ModelAttribute ShipingMethod delivery, Model model, HttpSession session){
        log.info("" + delivery.toString());

        ShipingMethod shipingMethod = customerService.getShippingMethodById(delivery.getShippingId());

        delivery.setPrice(shipingMethod.getPrice());
        delivery.setCompanyName(shipingMethod.getCompanyName());

        session.setAttribute("delivery", delivery);
        ShipingMethod sessionDelivery = (ShipingMethod) session.getAttribute("delivery");

        session.setAttribute("checkoutStep", 3);

        log.info("Session: " + sessionDelivery.toString());

        return "redirect:/paymentProcess";
    }

    @GetMapping("/listProducts")
    public String listProducts(Model model, Cart cart, HttpSession session) {
        log.info("listProducts called...");
        int pages;
        int currentPage = 1;

        ArrayList<Products> productList = customerService.getProducts();
        ArrayList<Products> list15 = new ArrayList<>();
        ArrayList<Integer> pageList = new ArrayList<>();

        pages = customerService.countPages(customerService.getProducts());

        pageList = customerService.getPageArray(pages);

        if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
        }

        if(productList.size()>15) {
            for(int i = 0; i<15;i++) {
                list15.add(productList.get(i));
            }
        }

        model.addAttribute("productList", list15);
        model.addAttribute("cart", cart);
        model.addAttribute("maxPages", pages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageArray", pageList);
        model.addAttribute("categories", customerService.getAllCategories());

        return "redirect:/listProducts/1";
    }

    @GetMapping("/listProducts/{page}")
    public String listProducts(@PathVariable("page") int page, Model model, HttpSession session) {
        ArrayList<Products> list15 = new ArrayList<>();
        ArrayList<Products> productList = customerService.getProducts();
        int maxPages = customerService.countPages(productList);
        ArrayList<Integer> pageList = customerService.getPageArray(maxPages);
        log.info("listProducts called... currentPage="+page+" maxPages="+maxPages);

        if(page>=1) {
            list15 = customerService.list15(productList, page);
        }



        model.addAttribute("productList", list15);
        model.addAttribute("currentPage", page);
        model.addAttribute("maxPages", maxPages);
        model.addAttribute("pageArray", pageList);
        model.addAttribute("categories", customerService.getAllCategories());

        return "listProducts";
    }
    @PostMapping("/listProducts/")
    public String listProducts(@RequestParam("page") int page) {

        return "redirect:/listProducts/"+page;
    }

    @PutMapping("/listProducts")
    public String listProducts(@ModelAttribute Cart cart, Model model, Cart cartNew, HttpSession session) {
        log.info("listProducts putmapping called...");

        List<Cart> sCart = (List<Cart>) session.getAttribute("cart");
        session.removeAttribute("cart");
        sCart.add(cart);
        session.setAttribute("cart", sCart);

        for (Cart testCart: cartList) {
            log.info(""+testCart.getProductId()+" amount:"+testCart.getAmount());
        }

        model.addAttribute("productList", customerService.getProducts());
        model.addAttribute("cart", cartNew);
        return "redirect:/listProducts/";
    }

    @GetMapping("viewProduct/{productId}")
    public String viewProduct(@PathVariable("productId") int productId, Model model, Cart cart, HttpSession session) {
        log.info("viewProduct called with id="+productId);

        if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
        }

        model.addAttribute("product", customerService.getProductById(productId));
        model.addAttribute("cart", cart);
        model.addAttribute("categories", customerService.getAllCategories());


        return "viewProduct";
    }

    @PutMapping("viewProduct/")
    public String viewProduct(@ModelAttribute Cart cart, Model model, HttpSession session) {
        log.info("viewProduct putmapping called with id="+cart.getProductId());
        Boolean check = false;

        for(int i = 0;i<cartList.size(); i++) {
            if(cart.getProductId()==cartList.get(i).getProductId()) {
                cartList.get(i).setAmount(cartList.get(i).getAmount()+cart.getAmount());
                check = true;
            }
        }
        if(check == false) {
            //cartList.add(cart);
            List<Cart> sCart = (List<Cart>) session.getAttribute("cart");
            session.removeAttribute("cart");
            sCart.add(cart);
            session.setAttribute("cart", sCart);
        }

        for (Cart testCart: cartList) {
            log.info(""+testCart.getProductId()+" amount:"+testCart.getAmount());
        }
        model.addAttribute("product",customerService.getProductById(cart.getProductId()));

        return "redirect:/viewProduct/"+cart.getProductId();
    }

    @GetMapping("/category/{categoryId}")
    public String category(@PathVariable("categoryId") int categoryId){

        return "redirect:/" + CATEGORY + "/" + categoryId + "/1";
    }

    @GetMapping("/category/{categoryId}/{page}")
    public String category(@PathVariable("categoryId") int categoryId ,@PathVariable("page") int page ,Model model, Cart cart, HttpSession session) {
        log.info("category called...");

        ArrayList<Products> list15 = new ArrayList<>();
        ArrayList<Products> productList = customerService.getProductsByCategory(categoryId);
        int maxPages = customerService.countPages(productList);
        ArrayList<Integer> pageList = customerService.getPageArray(maxPages);
        log.info("listProducts called... currentPage="+page+" maxPages="+maxPages);

        if(page>=1) {


            list15 = customerService.list15(productList, page);
        }

        if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
        }

        model.addAttribute("productList", list15);
        model.addAttribute("currentPage", page);
        model.addAttribute("maxPages", maxPages);
        model.addAttribute("pageArray", pageList);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("cart", cart);
        model.addAttribute("categoryName", customerService.getCategoryById(categoryId).getCategoryName());
        model.addAttribute("categories", customerService.getAllCategories());

        return CATEGORY;
    }

    @PutMapping("/category")
    public String category(@ModelAttribute Cart cart, Model model, Cart cartNew, HttpSession session) {
        log.info("category putmapping called...");

        //cartList.add(cart);

        int categoryId = customerService.getProductById(cart.getProductId()).getFk_categoryId();

        List<Cart> sCart = (List<Cart>) session.getAttribute("cart");
        session.removeAttribute("cart");
        sCart.add(cart);
        session.setAttribute("cart", sCart);

        for (Cart testCart: cartList) {
            log.info(""+testCart.getProductId()+" amount:"+testCart.getAmount());
        }


        model.addAttribute("productList", customerService.getProducts());
        model.addAttribute("cart", cartNew);
        model.addAttribute("categories", customerService.getAllCategories());

        return "redirect:/" + CATEGORY + "/" + categoryId;
    }

    @PostMapping("/category/{categoryId}")
    public String searchResult(@PathVariable("categoryId") int categoryId, @RequestParam("page") int page) {

        return "redirect:/category/"+categoryId+"/"+page;
    }

    @GetMapping("/search")
    public String search(Model model){
        log.info("search called...");

        return SEARCH;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam("searchProduct")String searchProduct, Model model, HttpSession session) throws  Exception{
        log.info("search requestmapping called");
        log.info("search word" + searchProduct);

        /*if(session.getAttribute("cart") == null){
            session.setAttribute("cart", cartList);
        }

        List<Products> searchResult = customerService.searchProduct(searchProduct);

        log.info("controller searchProduct result: " + searchResult.size());

        model.addAttribute("searchProduct", searchProduct);
        model.addAttribute("products", searchResult);*/

        return "redirect:/"+SEARCHRESULT+"/"+searchProduct+"/1";

    }

    @GetMapping("/searchResult/{searchInput}")
    public String searchResult(@PathVariable("searchInput") String searchInput){
        return "redirect:/"+SEARCHRESULT+"/"+searchInput+"/1";
    }

    @GetMapping("/searchResult/{searchInput}/{page}")
    public String searchResult(@PathVariable("searchInput") String searchInput, @PathVariable("page") int page, Model model, HttpSession session) {
        log.info("searchResult called... searchInput= "+searchInput+" page= "+page);

        ArrayList<Products> list15 = new ArrayList<>();
        ArrayList<Products> productList = customerService.searchProduct(searchInput);
        int maxPages = customerService.countPages(productList);
        ArrayList<Integer> pageList = customerService.getPageArray(maxPages);

        if(page>=1) {
            list15 = customerService.list15(productList, page);
        }

        model.addAttribute("productList", list15);
        model.addAttribute("currentPage", page);
        model.addAttribute("maxPages", maxPages);
        model.addAttribute("pageArray", pageList);
        model.addAttribute("searchInput", searchInput);
        model.addAttribute("categories", customerService.getAllCategories());

        return "searchResult";
    }

    @PostMapping("/searchResult/{searchInput}")
    public String searchResult(@PathVariable("searchInput") String searchInput, @RequestParam("page") int page) {

        return "redirect:/searchResult/"+searchInput+"/"+page;
    }

    @PutMapping("/searchResult")
    public String searchResult(@ModelAttribute Cart cart, Model model, Cart cartNew, HttpSession session) {
        log.info("category putmapping called...");

        //cartList.add(cart);

        int categoryId = customerService.getProductById(cart.getProductId()).getFk_categoryId();

        List<Cart> sCart = (List<Cart>) session.getAttribute("cart");
        session.removeAttribute("cart");
        sCart.add(cart);
        session.setAttribute("cart", sCart);

        for (Cart testCart: cartList) {
            log.info(""+testCart.getProductId()+" amount:"+testCart.getAmount());
        }


        model.addAttribute("productList", customerService.getProducts());
        model.addAttribute("cart", cartNew);
        return "redirect:/" + LISTPRODUCTS;
    }

    @GetMapping("/aboutUs")
    public String aboutUs(Model model) {
        AboutUs aboutUs = customerService.getAboutInfo(1);
        log.info("" + aboutUs.getPictureLink());
        model.addAttribute("aboutUs", aboutUs);
        model.addAttribute("categories", customerService.getAllCategories());

        return "aboutUs";
    }

    @GetMapping("/paymentProcess")
    public String paymentProcess(HttpSession session, Model model) {
        log.info("paymentProcess called...");

        if(session.getAttribute("checkoutStep") == null){
            session.setAttribute("checkoutStep", 0);
        }

        List<Products> productsList = customerService.getProducts();
        List<Cart> cart = (List<Cart>) session.getAttribute("cart");

        int checkoutStep = (int) session.getAttribute("checkoutStep");
        log.info("checkoutStep = " + checkoutStep);

        double total = 0;

        for(Cart c : cart){
            for(Products p : productsList){
                if (c.getProductId() == p.getProductId()) {
                    total = total + ( c.getAmount() * p.getPrice() );
                }
            }
        }
        model.addAttribute("categories", customerService.getAllCategories());
        if(checkoutStep > 2) {
            return "paymentProcess";
        } else {
            return "redirect:/delivery";
        }
    }

    @PostMapping("/creditcardAccept")
    public String paymentAccept(HttpSession session) {
        log.info("paymentAccept postmapping called...");

        List<Products> productsList = customerService.getProducts();
        List<Cart> cart = (List<Cart>) session.getAttribute("cart");
        Checkout checkout = (Checkout) session.getAttribute("checkout");
        ShipingMethod shipingMethod = (ShipingMethod) session.getAttribute("delivery");

        double total = 0;

        for(Cart c : cart){
            for(Products p : productsList){
                if (c.getProductId() == p.getProductId()) {
                    total = total + ( c.getAmount() * p.getPrice() );
                }
            }
        }

        total = total + shipingMethod.getPrice();

        customerService.createOrder(cart, checkout, shipingMethod, total);

        return "creditcardAccept";
    }

    @GetMapping("/editProduct/{productId}")
    public String editProduct (@PathVariable("productId") int productId, Model model ){
        log.info("editProduct getmapping is called ....");

        model.addAttribute("product", customerService.findProduct(productId));

        String productName = customerService.findProduct(productId).getName();
        model.addAttribute("productName", productName);
        model.addAttribute("categories", customerService.getAllCategories());

        return "editProduct";
    }

    @PutMapping ("/editProduct")
    public String editProduct (@ModelAttribute Products products,Model model) {
        log.info("editProducts putmapping called...");

        customerService.updateProducts(products);

        return "redirect:/adminListProducts";
    }

        @GetMapping("/addProduct")
    public String addProduct(Model model) {
        log.info("add product called...");

        model.addAttribute("categories", customerService.getAllCategories());
        model.addAttribute("product", new Products());

        return "addProduct";
    }

    @PostMapping("/addProduct")
    public String addProduct(@ModelAttribute Products product) {
        log.info("addProduct postmapping called...");

        log.info(product.getProductId() + " " + product.getDescription() + " ");
        customerService.addProduct(product);

        return "redirect:/adminListProducts";
    }

    @GetMapping("/adminListProducts")
    public String adminListProducts(Model model){
        log.info("AdminListProducts is called...");

        model.addAttribute("products", customerService.getAllProducts());
        model.addAttribute("categories",customerService.getAllCategories());

        return ADMINLISTPRODUCTS;
    }

    @RequestMapping(value = "/editStock", method = RequestMethod.POST)
    public String editCart(@RequestParam("productId")Integer productId, @RequestParam("stock")Integer stock) throws Exception {
        log.info("editStock is called...");

        customerService.updateStock(productId, stock);

        return "redirect:/adminListProducts";
    }

    @GetMapping("/removeProduct/{id}")
    public String removeProduct(@PathVariable("id") int id, Model model) {
        log.info("remove product getmapping called...");
        Products product = customerService.getProductById(id);

        model.addAttribute("product", product);
        model.addAttribute("categories", customerService.getAllCategories());

        return "removeProduct";
    }

    @PutMapping("/removeProduct")
    public String removeProduct(@ModelAttribute Products product) {
        log.info("removeProduct putmapping called...");
        log.info(product.getProductId()+"");
        customerService.removeProduct(product.getProductId());

        return "redirect:/";
    }
}
