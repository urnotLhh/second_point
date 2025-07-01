@SpringBootApplication(scanBasePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
@MapperScan(basePackages = RPanConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
@EnableAsync  //
//@EnableBinding(PanChannels.class)   // 在启动的时候，自动加载PanChannels.class的输入输出通道
public class RPanServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(RPanServerLauncher.class);
    }
}