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

    /** Channel�ڑ��^�C�v */
    private static final String CHANNEL_TYPE = "sftp";

    /**
     * �t�@�C���A�b�v���[�h
     *
     * @param hostname
     *            �ڑ���z�X�g
     * @param port
     *            �ڑ���|�[�g
     * @param userId
     *            �ڑ����郆�[�U
     * @param identityKeyFileName
     *            ���t�@�C����
     * @param sourcePath
     *            �A�b�v���[�h�Ώۃt�@�C���̃p�X<br>
     *            �A�v�����s����̐�΃p�X���w��
     * @param destPath
     *            �A�b�v��̃p�X
     * @throws JSchException
     *             Session�EChannel�̐ݒ�/�ڑ��G���[���ɔ���
     * @throws SftpException
     *             sftp���쎸�s���ɔ���
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
     * Session���J�n
     *
     * @param hostname
     *            �ڑ���z�X�g
     * @param port
     *            �ڑ���|�[�g
     * @param userId
     *            �ڑ����郆�[�U
     * @param identityKeyFileName
     *            ���t�@�C����
     */
    private Session connectSession(final String hostname, final int port,
            final String userId, final String identityKeyFileName)
            throws JSchException {

        final JSch jsch = new JSch();
        // ���ǉ�
        jsch.addIdentity(Thread.currentThread().getContextClassLoader()
                .getResource(identityKeyFileName).getFile());
        // Session�ݒ�
        final Session session = jsch.getSession(userId, hostname, port);
        final UserInfo userInfo = new SftpUserInfo();

        // TODO ����͎g�p���Ȃ����p�X�t���[�Y�����K�v�ȏꍇ��UserInfo�C���X�^���X�o�R�Őݒ肷��

        session.setUserInfo(userInfo);

        session.connect();

        return session;
    }

    /**
     * SFTP��Channel���J�n
     *
     * @param session
     *            �J�n���ꂽSession���
     */
    private ChannelSftp connectChannelSftp(final Session session)
            throws JSchException {
        final ChannelSftp channel = (ChannelSftp) session
                .openChannel(CHANNEL_TYPE);
        channel.connect();

        return channel;
    }

    /**
     * Session�EChannel�̏I��
     *
     * @param session
     *            �J�n���ꂽSession���
     * @param channels
     *            �J�n���ꂽChannel���.�����w��\
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
     * SFTP�ɐڑ����郆�[�U����ێ�����N���X
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