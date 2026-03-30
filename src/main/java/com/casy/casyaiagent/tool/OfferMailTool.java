package com.casy.casyaiagent.tool;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Offer邮件发送工具
 */
@Component
@Slf4j
public class OfferMailTool {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${offer.mail.company-name:示例科技有限公司}")
    private String companyName;

    @Value("${offer.mail.hr-name:HR团队}")
    private String hrName;

    @Value("${offer.mail.contact-phone:400-888-8888}")
    private String contactPhone;

    @Tool(description = "发送录用通知书(Offer)邮件给候选人，包含职位信息、薪资待遇、入职须知等")
    public String sendOfferEmail(
            @ToolParam(description = "候选人邮箱地址，例如：candidate@example.com") String toEmail,
            @ToolParam(description = "候选人姓名，例如：张三") String candidateName,
            @ToolParam(description = "职位名称，例如：高级Java工程师") String position,
            @ToolParam(description = "部门名称，例如：技术研发部") String department,
            @ToolParam(description = "入职日期，格式：yyyy-MM-dd，例如：2026-04-15") String entryDate,
            @ToolParam(description = "年薪（万元），例如：30") String annualSalary,
            @ToolParam(description = "工作地点，例如：上海市浦东新区") String workLocation) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("【录用通知】" + companyName + " - " + position + "职位 Offer");
            helper.setText(buildOfferHtml(candidateName, position, department, entryDate, annualSalary, workLocation), true);

            mailSender.send(message);
            log.info("Offer邮件已发送至: {}", toEmail);
            return "Offer邮件发送成功！已发送至: " + toEmail;
        } catch (MessagingException e) {
            log.error("发送Offer邮件失败: {}", e.getMessage(), e);
            return "发送Offer邮件失败: " + e.getMessage();
        }
    }

    /**
     * 构建Offer邮件HTML内容
     */
    private String buildOfferHtml(String candidateName, String position, String department,
                                  String entryDate, String annualSalary, String workLocation) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));

        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>录用通知书</title>\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { font-family: 'Segoe UI', 'Microsoft YaHei', Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; }\n" +
                "        .container { max-width: 800px; margin: 0 auto; background: #ffffff; border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden; }\n" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px; text-align: center; color: white; }\n" +
                "        .header h1 { font-size: 28px; font-weight: 600; letter-spacing: 2px; }\n" +
                "        .header .subtitle { margin-top: 10px; font-size: 14px; opacity: 0.9; }\n" +
                "        .content { padding: 40px; }\n" +
                "        .greeting { font-size: 18px; color: #333; margin-bottom: 20px; }\n" +
                "        .greeting .name { color: #667eea; font-weight: bold; font-size: 20px; }\n" +
                "        .offer-card { background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); border-radius: 12px; padding: 30px; margin: 25px 0; border-left: 5px solid #667eea; }\n" +
                "        .offer-card h2 { color: #667eea; font-size: 20px; margin-bottom: 20px; display: flex; align-items: center; }\n" +
                "        .offer-card h2::before { content: '\\2726'; margin-right: 10px; }\n" +
                "        .info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; }\n" +
                "        .info-item { background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }\n" +
                "        .info-item .label { font-size: 12px; color: #888; margin-bottom: 5px; text-transform: uppercase; letter-spacing: 1px; }\n" +
                "        .info-item .value { font-size: 16px; color: #333; font-weight: 600; }\n" +
                "        .info-item .value.highlight { color: #667eea; font-size: 18px; }\n" +
                "        .info-item.full-width { grid-column: 1 / -1; }\n" +
                "        .message { margin: 25px 0; padding: 20px; background: #f8f9fa; border-radius: 8px; line-height: 1.8; color: #555; }\n" +
                "        .message p { margin-bottom: 10px; }\n" +
                "        .next-steps { background: #667eea; color: white; padding: 25px; border-radius: 12px; margin: 25px 0; }\n" +
                "        .next-steps h3 { font-size: 18px; margin-bottom: 15px; }\n" +
                "        .next-steps ul { list-style: none; padding-left: 0; }\n" +
                "        .next-steps li { padding: 8px 0; padding-left: 25px; position: relative; }\n" +
                "        .next-steps li::before { content: '\\2714'; position: absolute; left: 0; color: #a8edea; }\n" +
                "        .footer { text-align: center; padding: 30px; background: #f8f9fa; color: #666; }\n" +
                "        .footer .company { font-size: 18px; font-weight: bold; color: #667eea; margin-bottom: 10px; }\n" +
                "        .footer .contact { font-size: 13px; margin-top: 10px; }\n" +
                "        .highlight-box { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; text-align: center; margin: 20px 0; }\n" +
                "        .highlight-box p { margin: 5px 0; }\n" +
                "        .date { text-align: right; color: #888; font-size: 14px; margin-top: 20px; }\n" +
                "        @media (max-width: 600px) { .info-grid { grid-template-columns: 1fr; } .header h1 { font-size: 22px; } .content { padding: 20px; } }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>🎉 录用通知书</h1>\n" +
                "            <div class=\"subtitle\">Job Offer Letter</div>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"greeting\">\n" +
                "                尊敬的 <span class=\"name\">" + candidateName + "</span> 先生/女士：\n" +
                "            </div>\n" +
                "            <div class=\"message\">\n" +
                "                <p>您好！</p>\n" +
                "                <p>非常高兴地通知您，经过严格的面试筛选，您已成功通过我司的招聘考核。我们诚挚地邀请您加入 <strong>" + companyName + "</strong>，成为我们团队的一员！</p>\n" +
                "                <p>我们相信您的加入将为团队带来新的活力，同时这里也将是您施展才华、实现职业梦想的舞台。</p>\n" +
                "            </div>\n" +
                "            <div class=\"offer-card\">\n" +
                "                <h2>录用详情</h2>\n" +
                "                <div class=\"info-grid\">\n" +
                "                    <div class=\"info-item\">\n" +
                "                        <div class=\"label\">职位名称</div>\n" +
                "                        <div class=\"value\">" + position + "</div>\n" +
                "                    </div>\n" +
                "                    <div class=\"info-item\">\n" +
                "                        <div class=\"label\">所属部门</div>\n" +
                "                        <div class=\"value\">" + department + "</div>\n" +
                "                    </div>\n" +
                "                    <div class=\"info-item\">\n" +
                "                        <div class=\"label\">入职日期</div>\n" +
                "                        <div class=\"value highlight\">" + entryDate + "</div>\n" +
                "                    </div>\n" +
                "                    <div class=\"info-item\">\n" +
                "                        <div class=\"label\">年薪待遇</div>\n" +
                "                        <div class=\"value highlight\">" + annualSalary + " 万元</div>\n" +
                "                    </div>\n" +
                "                    <div class=\"info-item full-width\">\n" +
                "                        <div class=\"label\">工作地点</div>\n" +
                "                        <div class=\"value\">" + workLocation + "</div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"highlight-box\">\n" +
                "                <p>🌟 我们为您提供：</p>\n" +
                "                <p>具有市场竞争力的薪酬福利 | 广阔的发展空间 | 舒适的办公环境 | 优秀的团队氛围</p>\n" +
                "            </div>\n" +
                "            <div class=\"next-steps\">\n" +
                "                <h3>📋 下一步行动</h3>\n" +
                "                <ul>\n" +
                "                    <li>请于收到本通知后 3 个工作日内确认是否接受此 Offer</li>\n" +
                "                    <li>确认接受后，我们将发送详细的入职指南和材料准备清单</li>\n" +
                "                    <li>如有任何疑问，请随时与 HR 联系</li>\n" +
                "                    <li>期待与您在入职日相见！</li>\n" +
                "                </ul>\n" +
                "            </div>\n" +
                "            <div class=\"date\">\n" +
                "                发送日期：" + currentDate + "\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <div class=\"company\">" + companyName + "</div>\n" +
                "            <div>人力资源部 - " + hrName + "</div>\n" +
                "            <div class=\"contact\">联系电话：" + contactPhone + " | 邮箱：" + fromEmail + "</div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
