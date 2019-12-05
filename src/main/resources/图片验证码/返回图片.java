@Autowired
    DefaultKaptcha defaultKaptcha;

@RequestMapping("/code/{deviceId}")
public void createCode(@PathVariable String deviceId, HttpServletResponse response) throws Exception {
        Assert.notNull(deviceId, "机器码不能为空");
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //生成文字验证码
        String text = defaultKaptcha.createText();
        //生成图片验证码
        BufferedImage image = defaultKaptcha.createImage(text);
        //生成的验证码写入redis
        jedisPool.getResource().setex(deviceId,60*5,text);
        //获取输出流
        ServletOutputStream out = response.getOutputStream();
        //将图片写回浏览器
        ImageIO.write(image, "JPEG", out);
        }