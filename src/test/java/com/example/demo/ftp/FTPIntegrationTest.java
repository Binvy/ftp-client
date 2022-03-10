package com.example.demo.ftp;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.KeyManagerUtils;
import org.apache.commons.net.util.TrustManagerUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.DefaultFtpsSessionFactory;
import org.springframework.integration.ftp.session.FtpSession;
import org.springframework.util.Assert;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

/**
 *
 * @author hanbinwei
 * @date 2022/3/3 16:47
 */
@SpringBootTest
public class FTPIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(FTPIntegrationTest.class);

    @Value("${ftp.host:localhost}")
    private String host;
    @Value("${ftp.port:21}")
    private Integer port;
    @Value("${ftp.username:hanbinwei}")
    private String username;
    @Value("${ftp.password:13752330376}")
    private String password;

    private static final String FTP_BASE_PATH = "/";

    private static final String LOCALE_BASE_PATH = "F:\\work\\venus\\file";

    @Test
    public void testFtp() {

        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_NT);
        factory.setConfig(config);
        FtpSession session = factory.getSession();

        Assert.isTrue(session.test(), "failed to test FTPClient");

        boolean isOpen = session.isOpen();
        logger.info("ftp session is open? {}", isOpen ? "yes" : "no");

        String path = FTP_BASE_PATH;
        String localPath = LOCALE_BASE_PATH;

        try {
            boolean exists = session.exists(path);
            logger.info("path {} is exists? {}", path, exists ? "yes" : "no");

            FTPFile[] ftpFiles = session.list(path);
            logger.info("ftp files: {}", ftpFiles);

            Arrays.stream(ftpFiles).forEach(ftpFile -> {
                if (ftpFile.isFile()) {
                    try {
                        String filePath = localPath + File.separator + ftpFile.getName();
                        session.read(path + "/" + ftpFile.getName(), new FileOutputStream(filePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            logger.error("ftp op exception.", e);
        }

    }

    /**
     * 全量的FtpSession配置信息
     */
    public FtpSession buildFtpSession() {

        int fileType = FTP.BINARY_FILE_TYPE;
        String controlEncoding = FTP.DEFAULT_CONTROL_ENCODING;
        FTPClientConfig ftpClientConfig = this.buildFTPClientConfig();
        int bufferSize = 2048;
        String host = this.host;
        Integer port = this.port;
        String username = this.username;
        String password = this.password;
        int clientMode = FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE;
        int connectTimeout = 6000;
        int defaultTimeout = 6000;
        int dataTimeout = 6000;

        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setFileType(fileType);
        factory.setControlEncoding(controlEncoding);
        factory.setConfig(ftpClientConfig);
        factory.setBufferSize(bufferSize);
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setClientMode(clientMode);
        factory.setConnectTimeout(connectTimeout);
        factory.setDefaultTimeout(defaultTimeout); // 命令连接超时时间
        factory.setDataTimeout(dataTimeout); // 数据连接超时时间
        return factory.getSession();
    }

    /**
     *
     * @return
     */
    public FtpSession buildFtpsSession() {

        // Spring支持的FTPClient通用属性
        int fileType = FTP.BINARY_FILE_TYPE;
        String controlEncoding = FTP.DEFAULT_CONTROL_ENCODING;
        FTPClientConfig ftpClientConfig = this.buildFTPClientConfig();
        int bufferSize = 2048;
        String host = this.host;
        Integer port = this.port;
        String username = this.username;
        String password = this.password;
        int clientMode = FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE;
        int connectTimeout = 6000;
        int defaultTimeout = 6000;
        int dataTimeout = 6000;

        // Spring支持的FTPSClient特有属性
        String protocol = "SSL";
        Boolean useClientMode = true;
        Boolean sessionCreation = true;
        String authValue = "TLS";
        TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        String[] cipherSuites = new String[]{"a", "b", "c"}; // default null
        String[] protocols = new String[]{"SSL", "TLS"};
        KeyManager keyManager = null;
        try {
            keyManager = KeyManagerUtils.createClientKeyManager("JKS",
                    new File("/path/to/privatekeystore.jks"),"storepassword",
                    "privatekeyalias", "keypassword");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        Boolean needClientAuth = false;
        Boolean wantsClientAuth = false;
        boolean implicit = false; //  The security mode(Implicit/Explicit).
        String prot = "P"; // org.apache.commons.net.ftp.FTPSClient.PROT_COMMAND_VALUE

        // FIXME 其他Spring未集成，但是FTPClient原生支持的属性，具体参考：org.apache.commons.net.ftp.FTPClient
        // 扩展方式如下：
        class AdvancedFtpSessionFactory extends DefaultFtpSessionFactory {
            protected void postProcessClientBeforeConnect(FTPClient ftpClient) throws IOException {
                ftpClient.setActivePortRange(4000, 5000);
            }
        }

        DefaultFtpsSessionFactory factory = new DefaultFtpsSessionFactory();

        // Spring支持的FTPClient通用属性设置
        factory.setFileType(fileType);
        factory.setControlEncoding(controlEncoding);
        factory.setConfig(ftpClientConfig);
        factory.setBufferSize(bufferSize);
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setClientMode(clientMode);
        factory.setConnectTimeout(connectTimeout);
        factory.setDefaultTimeout(defaultTimeout);
        factory.setDataTimeout(dataTimeout);

        // Spring支持的FTPSClient属性设置
        factory.setProtocol(protocol);
        factory.setUseClientMode(useClientMode);
        factory.setSessionCreation(sessionCreation);
        factory.setAuthValue(authValue);
        factory.setTrustManager(trustManager);
        factory.setCipherSuites(cipherSuites);
        factory.setProtocols(protocols);
        factory.setKeyManager(keyManager);
        factory.setNeedClientAuth(needClientAuth);
        factory.setWantsClientAuth(wantsClientAuth);
        factory.setProt(prot);
        factory.setImplicit(implicit);

        // FTPClient原生的其他属性设置


        return factory.getSession();
    }

    /**
     * 构建FTPClientConfig
     * 具体使用处参考： org.apache.commons.net.ftp.parser.FTPTimestampParserImpl
     * @return
     */
    public FTPClientConfig buildFTPClientConfig() {

        String serverSystemKey = FTPClientConfig.SYST_UNIX; // 服务器系统标识
        String dateFormat = new SimpleDateFormat().toPattern();
        String defaultDateFormatStr = dateFormat; // 默认日志格式，
        String recentDateFormatStr = dateFormat; // 最近的日志格式
        boolean lenientFutureDates = true; // NET-407 // 放宽未来日期
        String serverLanguageCode = "zh"; // 服务器语言代码 参考：org.apache.commons.net.ftp.FTPClientConfig.LANGUAGE_CODE_MAP
        String shortMonthNames = "jan|feb|mar|apr|maí|jun|jul|arg|sep|okt|nov|des"; // 短月份名称
        String serverTimeZoneId = TimeZone.getDefault().getID(); // 服务器时区
        boolean saveUnparseableEntries = true; // 如果解析失败，允许列表解析方法创建基本的 FTPFile 条目，默认false

        FTPClientConfig clientConfig = new FTPClientConfig(serverSystemKey);
        clientConfig.setDefaultDateFormatStr(defaultDateFormatStr);
        clientConfig.setRecentDateFormatStr(recentDateFormatStr);
        clientConfig.setLenientFutureDates(lenientFutureDates);
        clientConfig.setServerLanguageCode(serverLanguageCode);
        clientConfig.setShortMonthNames(shortMonthNames);
        clientConfig.setServerTimeZoneId(serverTimeZoneId);
        clientConfig.setUnparseableEntries(saveUnparseableEntries);
        return clientConfig;
    }

}
