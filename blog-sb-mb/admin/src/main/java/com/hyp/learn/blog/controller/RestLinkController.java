package com.hyp.learn.blog.controller;


import com.github.pagehelper.PageInfo;
import com.hyp.learn.blog.business.annotation.BussinessLog;
import com.hyp.learn.blog.business.entity.Link;
import com.hyp.learn.blog.business.enums.LinkSourceEnum;
import com.hyp.learn.blog.business.enums.ResponseStatus;
import com.hyp.learn.blog.business.enums.TemplateKeyEnum;
import com.hyp.learn.blog.business.srvice.MailService;
import com.hyp.learn.blog.business.srvice.SysLinkService;
import com.hyp.learn.blog.business.vo.LinkConditionVO;
import com.hyp.learn.blog.framework.object.PageResult;
import com.hyp.learn.blog.framework.object.ResponseVO;
import com.hyp.learn.blog.utils.ResultUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 友情链接
 */
@RestController
@RequestMapping("/link")
public class RestLinkController {
    @Autowired
    private SysLinkService linkService;
    @Autowired
    private MailService mailService;

    @RequiresPermissions("links")
    @PostMapping("/list")
    public PageResult list(LinkConditionVO vo) {
        PageInfo<Link> pageInfo = linkService.findPageBreakByCondition(vo);
        return ResultUtil.tablePage(pageInfo);
    }

    @RequiresPermissions("link:add")
    @PostMapping(value = "/add")
    @BussinessLog("添加友情链接")
    public ResponseVO add(Link link) {
        link.setSource(LinkSourceEnum.ADMIN);
        linkService.insert(link);
        mailService.send(link, TemplateKeyEnum.TM_LINKS);
        return ResultUtil.success("成功");
    }

    @RequiresPermissions(value = {"link:batchDelete", "link:delete"}, logical = Logical.OR)
    @PostMapping(value = "/remove")
    @BussinessLog("删除友情链接")
    public ResponseVO remove(Long[] ids) {
        if (null == ids) {
            return ResultUtil.error(500, "请至少选择一条记录");
        }
        for (Long id : ids) {
            linkService.removeByPrimaryKey(id);
        }
        return ResultUtil.success("成功删除 [" + ids.length + "] 个友情链接");
    }

    @RequiresPermissions("link:get")
    @PostMapping("/get/{id}")
    @BussinessLog("获取友情链接详情")
    public ResponseVO get(@PathVariable Long id) {
        return ResultUtil.success(null, this.linkService.getByPrimaryKey(id));
    }

    @RequiresPermissions("link:edit")
    @PostMapping("/edit")
    @BussinessLog("编辑友情链接")
    public ResponseVO edit(Link link) {
        try {
            linkService.updateSelective(link);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("友情链接修改失败！");
        }
        return ResultUtil.success(ResponseStatus.SUCCESS);
    }

}