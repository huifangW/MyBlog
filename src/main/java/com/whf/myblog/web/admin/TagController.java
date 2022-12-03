package com.whf.myblog.web.admin;

import com.whf.myblog.po.Tag;
import com.whf.myblog.service.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("admin")
public class TagController {
    @Autowired
    private TagServiceImpl tagService;

    @GetMapping("/tags")
    public String index(@PageableDefault(size = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        Page<Tag> tags = tagService.listTag(pageable);
        model.addAttribute("tags", tags);
        if(tags.getTotalElements() == 0) {
            model.addAttribute("message", "暂无标签信息");
        }
        return "/admin/tags";
    }

    @PostMapping("/tag/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("name") String name,
                       RedirectAttributes redirectAttributes,
                       @Valid Tag tag,
                       BindingResult result) {

        // 数据校验错误
        if(result.hasErrors()) {
            String nameMsg = "输入名称：[" + name + "]";
            String errorMsg = "错误消息：[" + result.getFieldError().getDefaultMessage() + "]";
            redirectAttributes.addFlashAttribute("errorMessage", nameMsg + "<br>" + errorMsg);
            return "redirect:/admin/tags";
        }

        // 重复登录错误
        Tag sameNameType = tagService.getByName(name);
        if(sameNameType != null) {
            String nameMsg = "输入名称：[" + name + "]";
            String errorMsg = "错误消息：[该标签已存在，不能重复登录]";
            redirectAttributes.addFlashAttribute("errorMessage", nameMsg + "<br>" + errorMsg);
            return "redirect:/admin/tags";
        }

        Tag newTag = new Tag(id, name);

        if (id == null) {
            tagService.saveTag(newTag);
            redirectAttributes.addFlashAttribute("message", "登录成功");
        } else {
            tagService.updateTag(id, newTag);
            redirectAttributes.addFlashAttribute("message", "更新成功");
        }

        return "redirect:/admin/tags";
    }

    @PostMapping("/tag/delete")
    public String delete(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        tagService.deleteTag(id);
        redirectAttributes.addFlashAttribute("message", "删除成功");

        return "redirect:/admin/tags";
    }
}
