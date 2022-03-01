package dev.cuican.staypro.module.pingbypass;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.time.StopWatch;


@SuppressWarnings("unused")
@ModuleInfo(name = "PingBypass", category = Category.MISC, description = "PingBypass")
public class PingBypass extends Module
{


    final Setting<String> ip = setting("IP", "Proxy-IP");
    final Setting<String> port = setting("Port", "0");
    final Setting<Boolean> noRender = setting("NoRender", false);
    final Setting<Integer> pings   = setting("Pings", 5000, 500, 10000);



    StopWatch timer = new StopWatch();
    long startTime;
    int serverPing;
    long ping;
    boolean handled;





    @Override
    public void onEnable()
    {
        disconnectFromMC();
    }
    public static void disconnectFromMC()
    {
        NetHandlerPlayClient connection = mc.getConnection();
        if(connection != null)
        {
            connection.getNetworkManager().closeChannel(new TextComponentString("Quitting"));
        }
    }
    @Override
    public void onDisable()
    {
        disconnectFromMC();
    }

    @Override
    public String getModuleInfo()
    {
        return ping + "ms";
    }

    public long getPing()
    {
        return ping;
    }

    public int getServerPing()
    {
        return serverPing;
    }

    public String getIp()
    {
        return ip.getValue();
    }

    public void setIp(String ip)
    {
        this.ip.setValue(ip);
    }

    public void setPort(String port)
    {
        this.port.setValue(port);
    }

    public String getPortAsString()
    {
        return port.getValue();
    }

    public int getPort()
    {
        try
        {
            return Integer.parseInt(port.getValue());
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
}
