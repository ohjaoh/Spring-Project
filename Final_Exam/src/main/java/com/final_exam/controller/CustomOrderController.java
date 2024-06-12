// CustomOrderController.java
package com.final_exam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.final_exam.entity.CustomOrder;
import com.final_exam.entity.Member;
import com.final_exam.entity.Product;
import com.final_exam.service.CustomOrderService;
import com.final_exam.service.MemberService;
import com.final_exam.service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class CustomOrderController {

    @Autowired
    private CustomOrderService customOrderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberService memberService;

    // 맞춤 주문 관리 페이지를 반환합니다.
    @GetMapping("/custom-orders-management")
    public String viewCustomOrderManagementPage() {
        return "custom-order-management";
    }

    // 새로운 맞춤 주문 추가 페이지로 이동
    @GetMapping("/custom-orders/new")
    public String showAddCustomOrderForm(Model model, HttpSession session) {
        LoginController.User user = (LoginController.User) session.getAttribute("user");
        if (user != null) {
            Member member = memberService.findById(user.getId());
            CustomOrder customOrder = new CustomOrder();
            customOrder.setMember(member); // 회원 정보를 맞춤 주문에 설정

            List<Product> products = productService.getAllProducts();
            model.addAttribute("customOrder", customOrder);
            model.addAttribute("products", products);
            session.setAttribute("visitedCustomOrderForm", true);
            return "custom-order-form";
        }
        return "redirect:/login";
    }

    // 새로운 맞춤 주문 추가 처리
    @PostMapping("/custom-orders")
    public String addCustomOrder(@Valid @ModelAttribute("customOrder") CustomOrder customOrder, BindingResult result,
                                 Model model, HttpSession session) {
        if (result.hasErrors()) {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            return "custom-order-form";
        }

        // 회원 정보 설정
        LoginController.User user = (LoginController.User) session.getAttribute("user");
        if (user != null) {
            Member member = memberService.findById(user.getId());
            if (member != null) {
                customOrder.setMember(member);
                System.out.println("Member set with ID: " + member.getId() + ", RealName: " + member.getRealName()); // 디버깅 로그 출력
            } else {
                System.out.println("Member not found for ID: " + user.getId());
            }
        } else {
            System.out.println("User not found in session");
        }

        // 디버깅: 주문 정보 출력
        System.out.println("CustomOrder details:");
        System.out.println("Product Code: " + customOrder.getProductCode());
        System.out.println("Customization Details: " + customOrder.getCustomizationDetails());
        System.out.println("Delivery Date: " + customOrder.getDeliveryDate());
        System.out.println("Special Instructions: " + customOrder.getSpecialInstructions());
        System.out.println("Order Name: " + customOrder.getOrderName());
        System.out.println("Order Phone Number: " + customOrder.getOrderPhoneNumber());
        System.out.println("Order Address: " + customOrder.getOrderAddress());

        customOrderService.saveCustomOrder(customOrder);
        System.out.println("Custom order saved with product code: " + customOrder.getProductCode());
        session.setAttribute("customOrderSaved", true);
        session.removeAttribute("visitedCustomOrderForm");
        return "redirect:/custom-orders";
    }

    // 맞춤 주문 목록 페이지로 이동
    @GetMapping("/custom-orders")
    public String viewCustomOrderList(Model model) {
        List<CustomOrder> customOrders = customOrderService.getAllCustomOrders();
        model.addAttribute("customOrders", customOrders);
        return "custom-order-list";
    }

    // 맞춤 주문 수정 페이지로 이동
    @GetMapping("/custom-orders/edit/{id}")
    public String showEditCustomOrderForm(@PathVariable("id") int id, Model model, HttpSession session) {
        CustomOrder customOrder = customOrderService.getCustomOrderById(id);
        List<Product> products = productService.getAllProducts();
        model.addAttribute("customOrder", customOrder);
        model.addAttribute("products", products);
        session.setAttribute("visitedEditCustomOrderForm", true);
        return "custom-order-edit";
    }

    // 맞춤 주문 수정 처리
    @PostMapping("/custom-orders/edit/{id}")
    public String editCustomOrder(@PathVariable("id") int id,
                                  @Valid @ModelAttribute("customOrder") CustomOrder customOrder, BindingResult result, Model model,
                                  HttpSession session) {
        if (result.hasErrors()) {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            return "custom-order-form";
        }

        // 회원 정보 설정
        LoginController.User user = (LoginController.User) session.getAttribute("user");
        if (user != null) {
            Member member = memberService.findById(user.getId());
            customOrder.setMember(member);
        }

        customOrderService.updateCustomOrder(id, customOrder);
        session.setAttribute("customOrderUpdated", true);
        session.removeAttribute("visitedEditCustomOrderForm");
        return "redirect:/custom-orders";
    }

    // 맞춤 주문 삭제 처리
    @DeleteMapping("/custom-orders/delete/{id}")
    public String deleteCustomOrder(@PathVariable("id") int id, HttpSession session) {
        customOrderService.deleteCustomOrder(id);
        session.setAttribute("customOrderDeleted", true);
        return "redirect:/custom-orders";
    }

}
