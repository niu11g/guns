package com.stylefeng.guns;

import com.stylefeng.guns.rest.AlipayApplication;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlipayApplication.class)
public class GunsRestApplicationTests {
	@Autowired
	private FTPUtil ftpUtil;

	@Test
	public void contextLoads() {
		File file = new File("C:\\Users\\Administrator\\Desktop\\仿猫眼项目\\支付模块\\qrcode\\qr-415sdf58ew12ds5fe1.png");

		boolean isSuccess = ftpUtil.uploadFile("qr-415sdf58ew12ds5fe1.png", file);
		System.out.print("上传是否成功!"+isSuccess);
	}

}
