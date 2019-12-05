@Autowired
private JedisPool jedisPool;
//图片上传
@RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile")MultipartFile imgFile){
        try{
        //获取原始文件名
        String originalFilename = imgFile.getOriginalFilename();
        int lastIndexOf = originalFilename.lastIndexOf(".");
        //获取文件后缀
        String suffix = originalFilename.substring(lastIndexOf - 1);
        //使用UUID随机产生文件名称，防止同名文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
        //图片上传成功
        Result result = new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS);
        result.setData(fileName);
        //将上传图片名称存入Redis，基于Redis的Set集合存储
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        return result;
        }catch (Exception e){
        e.printStackTrace();
        //图片上传失败
        return new Result(false,MessageConstant.PIC_UPLOAD_FAIL);
        }
        }

        /*----------------------------------------------------------------------------------*/
      @Autowired
      private JedisPool jedisPool;
      //新增套餐
public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        if(checkgroupIds != null && checkgroupIds.length > 0){
        setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        }
        //将图片名称保存到Redis
        savePic2Redis(setmeal.getImg());
        }
//将图片名称保存到Redis
private void savePic2Redis(String pic){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
        }