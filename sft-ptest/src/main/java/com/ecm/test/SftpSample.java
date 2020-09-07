package com.ecm.test;

import java.util.Arrays;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class SftpSample {

    /** Channel接続タイプ */
    private static final String CHANNEL_TYPE = "sftp";

    /**
     * ファイルアップロード
     *
     * @param hostname
     *            接続先ホスト
     * @param port
     *            接続先ポート
     * @param userId
     *            接続するユーザ
     * @param identityKeyFileName
     *            鍵ファイル名
     * @param sourcePath
     *            アップロード対象ファイルのパス<br>
     *            アプリ実行環境上の絶対パスを指定
     * @param destPath
     *            アップ先のパス
     * @throws JSchException
     *             Session・Channelの設定/接続エラー時に発生
     * @throws SftpException
     *             sftp操作失敗時に発生
     */
    public void putFile(final String hostname, final int port,
            final String userId, final String identityKeyFileName,
            final String sourcePath, final String destPath)
            throws JSchException, SftpException {

        Session session = null;
        ChannelSftp channel = null;

        try {
            session = connectSession(hostname, port, userId, identityKeyFileName);
            channel = connectChannelSftp(session);
            channel.put(sourcePath, destPath);
        } finally {
            disconnect(session, channel);
        }
    }

    /**
     * Sessionを開始
     *
     * @param hostname
     *            接続先ホスト
     * @param port
     *            接続先ポート
     * @param userId
     *            接続するユーザ
     * @param identityKeyFileName
     *            鍵ファイル名
     */
    private Session connectSession(final String hostname, final int port,
            final String userId, final String identityKeyFileName)
            throws JSchException {

        final JSch jsch = new JSch();
        // 鍵追加
        jsch.addIdentity(Thread.currentThread().getContextClassLoader()
                .getResource(identityKeyFileName).getFile());
        // Session設定
        final Session session = jsch.getSession(userId, hostname, port);
        final UserInfo userInfo = new SftpUserInfo();

        // TODO 今回は使用しないがパスフレーズ等が必要な場合はUserInfoインスタンス経由で設定する

        session.setUserInfo(userInfo);

        session.connect();

        return session;
    }

    /**
     * SFTPのChannelを開始
     *
     * @param session
     *            開始されたSession情報
     */
    private ChannelSftp connectChannelSftp(final Session session)
            throws JSchException {
        final ChannelSftp channel = (ChannelSftp) session
                .openChannel(CHANNEL_TYPE);
        channel.connect();

        return channel;
    }

    /**
     * Session・Channelの終了
     *
     * @param session
     *            開始されたSession情報
     * @param channels
     *            開始されたChannel情報.複数指定可能
     */
    private void disconnect(final Session session, final Channel... channels) {
        if (channels != null) {
            Arrays.stream(channels).forEach(c -> {
                if (c != null) {
                    c.disconnect();
                }
            });
        }
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * SFTPに接続するユーザ情報を保持するクラス
     */
    private static class SftpUserInfo implements UserInfo {

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassword(String arg0) {
            return true;
        }

        @Override
        public boolean promptPassphrase(String arg0) {
            return true;
        }

        @Override
        public boolean promptYesNo(String arg0) {
            return true;
        }

        @Override
        public void showMessage(String arg0) {
        }

        @Override
        public String getPassphrase() {
            return null;
        }
    }

    public static void main(String[] args) throws JSchException, SftpException {

        final SftpSample sftp = new SftpSample();
        sftp.putFile("hostName", 22, "user", "xxxxx_rsa",
                "/Users/xxxxx/test.jpg", "/tmp/test.jpg");
    }
}