package com.hyp.learn.blog.controller;


import com.github.pagehelper.PageInfo;
import com.hyp.learn.blog.business.annotation.BussinessLog;
import com.hyp.learn.blog.business.entity.Comment;
import com.hyp.learn.blog.business.enums.ResponseStatus;
import com.hyp.learn.blog.business.enums.TemplateKeyEnum;
import com.hyp.learn.blog.business.srvice.MailService;
import com.hyp.learn.blog.business.srvice.PxCommentService;
import com.hyp.learn.blog.business.vo.CommentConditionVO;
import com.hyp.learn.blog.framework.exception.PxCommentException;
import com.hyp.learn.blog.framework.object.PageResult;
import com.hyp.learn.blog.framework.object.ResponseVO;
import com.hyp.learn.blog.utils.ResultUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论管理
 */
@RestController
@RequestMapping("/comment")
public class RestCommentController {
    @Autowired
    private PxCommentService commentService;
    @Autowired
    private MailService mailService;

    @RequiresPermissions("comments")
    @PostMapping("/list")
    public PageResult list(CommentConditionVO vo) {
        PageInfo<Comment> pageInfo = commentService.findPageBreakByCondition(vo);
        return ResultUtil.tablePage(pageInfo);
    }

    @RequiresPermissions("comment:reply")
    @PostMapping(value = "/reply")
    @BussinessLog("回复评论")
    public ResponseVO reply(Comment comment) {
        try {
            commentService.commentForAdmin(comment);
        } catch (PxCommentException e) {
            return ResultUtil.error(e.getMessage());
        }
        return ResultUtil.success("成功");
    }

    @RequiresPermissions(value = {"comment:batchDelete", "comment:delete"}, logical = Logical.OR)
    @PostMapping(value = "/remove")
    @BussinessLog("删除评论[{1}]")
    public ResponseVO remove(Long[] ids) {
        if (null == ids) {
            return ResultUtil.error(500, "请至少选择一条记录");
        }
        for (Long id : ids) {
            commentService.removeByPrimaryKey(id);
        }
        return ResultUtil.success("成功删除 [" + ids.length + "] 条评论");
    }

    @RequiresPermissions("comments")
    @PostMapping("/get/{id}")
    @BussinessLog("获取评论[{1}]详情")
    public ResponseVO get(@PathVariable Long id) {
        return ResultUtil.success(null, this.commentService.getByPrimaryKey(id));
    }

    @RequiresPermissions("comments")
    @PostMapping("/edit")
    @BussinessLog("编辑评论")
    public ResponseVO edit(Comment comment) {
        try {
            commentService.updateSelective(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("评论修改失败！");
        }
        return ResultUtil.success(ResponseStatus.SUCCESS);
    }

    @RequiresPermissions("comment:audit")
    @PostMapping("/audit")
    @BussinessLog("审核评论")
    public ResponseVO audit(Comment comment, String contentText, Boolean sendEmail) {
        try {
            commentService.updateSelective(comment);
            if (!StringUtils.isEmpty(contentText)) {
                comment.setContent(contentText);
                commentService.commentForAdmin(comment);
            }
            if (null != sendEmail && sendEmail) {
                Comment commentDB = commentService.getByPrimaryKey(comment.getId());
                mailService.send(commentDB, TemplateKeyEnum.TM_COMMENT_AUDIT, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("评论审核失败！");
        }
        return ResultUtil.success(ResponseStatus.SUCCESS);
    }

    /**
     * 正在审核的
     *
     * @param comment
     * @return
     */
    @RequiresUser
    @PostMapping("/listVerifying")
    public ResponseVO listVerifying(Comment comment) {
        return ResultUtil.success(null, commentService.listVerifying(10));
    }

}
