package com.whf.myblog.web.admin;

import com.whf.myblog.po.Type;
import com.whf.myblog.service.TypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("admin")
public class TypeController {
    @Autowired
    private TypeServiceImpl typeService;

    @GetMapping("/types")
    public String index(@PageableDefault(size = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        Page<Type> types = typeService.listType(pageable);
        model.addAttribute("types", types);
        if(types.getTotalElements() == 0) {
            model.addAttribute("message", "暂无分类信息");
        }
        return "/admin/types";
    }

    @PostMapping("/type/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("name") String name,
                       RedirectAttributes redirectAttributes,
                       @Valid @ModelAttribute("type") Type type,
                       BindingResult result) {

        // 数据校验错误
        if(result.hasErrors()) {
            String nameMsg = "输入名称：[" + name + "]";
            String errorMsg = "错误消息：[" + result.getFieldError().getDefaultMessage() + "]";
            redirectAttributes.addFlashAttribute("errorMessage", nameMsg + "<br>" + errorMsg);
            return "redirect:/admin/types";
        }

        // 重复登录错误
        Type sameNameType = typeService.getByName(name);
        if(sameNameType != null) {
            String nameMsg = "输入名称：[" + name + "]";
            String errorMsg = "错误消息：[该分类已存在，不能重复登录]";
            redirectAttributes.addFlashAttribute("errorMessage", nameMsg + "<br>" + errorMsg);
            return "redirect:/admin/types";
        }

        Type newType = new Type(id, name);

        if (id == null) {
            typeService.saveType(newType);
            redirectAttributes.addFlashAttribute("message", "登录成功");
        } else {
            typeService.updateType(id, newType);
            redirectAttributes.addFlashAttribute("message", "更新成功");
        }

        return "redirect:/admin/types";
    }

    @PostMapping("/type/delete")
    public String delete(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        typeService.deleteType(id);
        redirectAttributes.addFlashAttribute("message", "删除成功");

        return "redirect:/admin/types";
    }
}
