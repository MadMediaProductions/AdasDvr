package com.fvision.camera.utils;

import java.util.List;

public class DoCmdUtil {
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_SU = "su";
    public static String[] cmd = {"mount -o remount rw /system"};

    private DoCmdUtil() {
        throw new AssertionError();
    }

    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : (String[]) commands.toArray(new String[0]), isRoot, true);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : (String[]) commands.toArray(new String[0]), isRoot, isNeedResultMsg);
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x015b  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x006e A[SYNTHETIC, Splitter:B:27:0x006e] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0073 A[Catch:{ IOException -> 0x012d }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0078 A[Catch:{ IOException -> 0x012d }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00f8 A[SYNTHETIC, Splitter:B:66:0x00f8] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00fd A[Catch:{ IOException -> 0x0133 }] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0102 A[Catch:{ IOException -> 0x0133 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x013b A[SYNTHETIC, Splitter:B:92:0x013b] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0140 A[Catch:{ IOException -> 0x014e }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0145 A[Catch:{ IOException -> 0x014e }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x014a  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:63:0x00f3=Splitter:B:63:0x00f3, B:24:0x0069=Splitter:B:24:0x0069} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static CommandResult execCommand(String[] r21, boolean r22, boolean r23) {
        /*
            r12 = -1
            if (r21 == 0) goto L_0x000a
            r0 = r21
            int r0 = r0.length
            r18 = r0
            if (r18 != 0) goto L_0x001a
        L_0x000a:
            com.fvision.camera.utils.DoCmdUtil$CommandResult r18 = new com.fvision.camera.utils.DoCmdUtil$CommandResult
            r19 = 0
            r20 = 0
            r0 = r18
            r1 = r19
            r2 = r20
            r0.<init>(r12, r1, r2)
        L_0x0019:
            return r18
        L_0x001a:
            r11 = 0
            r16 = 0
            r7 = 0
            r14 = 0
            r5 = 0
            r9 = 0
            java.lang.Runtime r19 = java.lang.Runtime.getRuntime()     // Catch:{ IOException -> 0x0196, Exception -> 0x017c }
            if (r22 == 0) goto L_0x0050
            java.lang.String r18 = "su"
        L_0x0029:
            r0 = r19
            r1 = r18
            java.lang.Process r11 = r0.exec(r1)     // Catch:{ IOException -> 0x0196, Exception -> 0x017c }
            java.io.DataOutputStream r10 = new java.io.DataOutputStream     // Catch:{ IOException -> 0x0196, Exception -> 0x017c }
            java.io.OutputStream r18 = r11.getOutputStream()     // Catch:{ IOException -> 0x0196, Exception -> 0x017c }
            r0 = r18
            r10.<init>(r0)     // Catch:{ IOException -> 0x0196, Exception -> 0x017c }
            r0 = r21
            int r0 = r0.length     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            r19 = r0
            r18 = 0
        L_0x0043:
            r0 = r18
            r1 = r19
            if (r0 >= r1) goto L_0x0098
            r3 = r21[r18]     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            if (r3 != 0) goto L_0x0053
        L_0x004d:
            int r18 = r18 + 1
            goto L_0x0043
        L_0x0050:
            java.lang.String r18 = "sh"
            goto L_0x0029
        L_0x0053:
            byte[] r20 = r3.getBytes()     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            r0 = r20
            r10.write(r0)     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            java.lang.String r20 = "\n"
            r0 = r20
            r10.writeBytes(r0)     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            r10.flush()     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            goto L_0x004d
        L_0x0067:
            r4 = move-exception
            r9 = r10
        L_0x0069:
            r4.printStackTrace()     // Catch:{ all -> 0x0138 }
            if (r9 == 0) goto L_0x0071
            r9.close()     // Catch:{ IOException -> 0x012d }
        L_0x0071:
            if (r16 == 0) goto L_0x0076
            r16.close()     // Catch:{ IOException -> 0x012d }
        L_0x0076:
            if (r7 == 0) goto L_0x007b
            r7.close()     // Catch:{ IOException -> 0x012d }
        L_0x007b:
            if (r11 == 0) goto L_0x0080
            r11.destroy()
        L_0x0080:
            com.fvision.camera.utils.DoCmdUtil$CommandResult r19 = new com.fvision.camera.utils.DoCmdUtil$CommandResult
            if (r14 != 0) goto L_0x0153
            r18 = 0
            r20 = r18
        L_0x0088:
            if (r5 != 0) goto L_0x015b
            r18 = 0
        L_0x008c:
            r0 = r19
            r1 = r20
            r2 = r18
            r0.<init>(r12, r1, r2)
            r18 = r19
            goto L_0x0019
        L_0x0098:
            java.lang.String r18 = "exit\n"
            r0 = r18
            r10.writeBytes(r0)     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            r10.flush()     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            int r12 = r11.waitFor()     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            if (r23 == 0) goto L_0x0111
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            r15.<init>()     // Catch:{ IOException -> 0x0067, Exception -> 0x017f, all -> 0x0161 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0199, Exception -> 0x0183, all -> 0x0164 }
            r6.<init>()     // Catch:{ IOException -> 0x0199, Exception -> 0x0183, all -> 0x0164 }
            java.io.BufferedReader r17 = new java.io.BufferedReader     // Catch:{ IOException -> 0x019e, Exception -> 0x0188, all -> 0x0168 }
            java.io.InputStreamReader r18 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x019e, Exception -> 0x0188, all -> 0x0168 }
            java.io.InputStream r19 = r11.getInputStream()     // Catch:{ IOException -> 0x019e, Exception -> 0x0188, all -> 0x0168 }
            r18.<init>(r19)     // Catch:{ IOException -> 0x019e, Exception -> 0x0188, all -> 0x0168 }
            r17.<init>(r18)     // Catch:{ IOException -> 0x019e, Exception -> 0x0188, all -> 0x0168 }
            java.io.BufferedReader r8 = new java.io.BufferedReader     // Catch:{ IOException -> 0x01a4, Exception -> 0x018e, all -> 0x016d }
            java.io.InputStreamReader r18 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x01a4, Exception -> 0x018e, all -> 0x016d }
            java.io.InputStream r19 = r11.getErrorStream()     // Catch:{ IOException -> 0x01a4, Exception -> 0x018e, all -> 0x016d }
            r18.<init>(r19)     // Catch:{ IOException -> 0x01a4, Exception -> 0x018e, all -> 0x016d }
            r0 = r18
            r8.<init>(r0)     // Catch:{ IOException -> 0x01a4, Exception -> 0x018e, all -> 0x016d }
        L_0x00d0:
            java.lang.String r13 = r17.readLine()     // Catch:{ IOException -> 0x00da, Exception -> 0x00ec, all -> 0x0174 }
            if (r13 == 0) goto L_0x00e2
            r15.append(r13)     // Catch:{ IOException -> 0x00da, Exception -> 0x00ec, all -> 0x0174 }
            goto L_0x00d0
        L_0x00da:
            r4 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            r7 = r8
            r16 = r17
            goto L_0x0069
        L_0x00e2:
            java.lang.String r13 = r8.readLine()     // Catch:{ IOException -> 0x00da, Exception -> 0x00ec, all -> 0x0174 }
            if (r13 == 0) goto L_0x010c
            r6.append(r13)     // Catch:{ IOException -> 0x00da, Exception -> 0x00ec, all -> 0x0174 }
            goto L_0x00e2
        L_0x00ec:
            r4 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            r7 = r8
            r16 = r17
        L_0x00f3:
            r4.printStackTrace()     // Catch:{ all -> 0x0138 }
            if (r9 == 0) goto L_0x00fb
            r9.close()     // Catch:{ IOException -> 0x0133 }
        L_0x00fb:
            if (r16 == 0) goto L_0x0100
            r16.close()     // Catch:{ IOException -> 0x0133 }
        L_0x0100:
            if (r7 == 0) goto L_0x0105
            r7.close()     // Catch:{ IOException -> 0x0133 }
        L_0x0105:
            if (r11 == 0) goto L_0x0080
            r11.destroy()
            goto L_0x0080
        L_0x010c:
            r5 = r6
            r14 = r15
            r7 = r8
            r16 = r17
        L_0x0111:
            if (r10 == 0) goto L_0x0116
            r10.close()     // Catch:{ IOException -> 0x0128 }
        L_0x0116:
            if (r16 == 0) goto L_0x011b
            r16.close()     // Catch:{ IOException -> 0x0128 }
        L_0x011b:
            if (r7 == 0) goto L_0x0120
            r7.close()     // Catch:{ IOException -> 0x0128 }
        L_0x0120:
            if (r11 == 0) goto L_0x01ac
            r11.destroy()
            r9 = r10
            goto L_0x0080
        L_0x0128:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x0120
        L_0x012d:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x007b
        L_0x0133:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x0105
        L_0x0138:
            r18 = move-exception
        L_0x0139:
            if (r9 == 0) goto L_0x013e
            r9.close()     // Catch:{ IOException -> 0x014e }
        L_0x013e:
            if (r16 == 0) goto L_0x0143
            r16.close()     // Catch:{ IOException -> 0x014e }
        L_0x0143:
            if (r7 == 0) goto L_0x0148
            r7.close()     // Catch:{ IOException -> 0x014e }
        L_0x0148:
            if (r11 == 0) goto L_0x014d
            r11.destroy()
        L_0x014d:
            throw r18
        L_0x014e:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x0148
        L_0x0153:
            java.lang.String r18 = r14.toString()
            r20 = r18
            goto L_0x0088
        L_0x015b:
            java.lang.String r18 = r5.toString()
            goto L_0x008c
        L_0x0161:
            r18 = move-exception
            r9 = r10
            goto L_0x0139
        L_0x0164:
            r18 = move-exception
            r9 = r10
            r14 = r15
            goto L_0x0139
        L_0x0168:
            r18 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            goto L_0x0139
        L_0x016d:
            r18 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            r16 = r17
            goto L_0x0139
        L_0x0174:
            r18 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            r7 = r8
            r16 = r17
            goto L_0x0139
        L_0x017c:
            r4 = move-exception
            goto L_0x00f3
        L_0x017f:
            r4 = move-exception
            r9 = r10
            goto L_0x00f3
        L_0x0183:
            r4 = move-exception
            r9 = r10
            r14 = r15
            goto L_0x00f3
        L_0x0188:
            r4 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            goto L_0x00f3
        L_0x018e:
            r4 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            r16 = r17
            goto L_0x00f3
        L_0x0196:
            r4 = move-exception
            goto L_0x0069
        L_0x0199:
            r4 = move-exception
            r9 = r10
            r14 = r15
            goto L_0x0069
        L_0x019e:
            r4 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            goto L_0x0069
        L_0x01a4:
            r4 = move-exception
            r9 = r10
            r5 = r6
            r14 = r15
            r16 = r17
            goto L_0x0069
        L_0x01ac:
            r9 = r10
            goto L_0x0080
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fvision.camera.utils.DoCmdUtil.execCommand(java.lang.String[], boolean, boolean):com.fvision.camera.utils.DoCmdUtil$CommandResult");
    }

    public static class CommandResult {
        public String errorMsg;
        public int result;
        public String successMsg;

        public CommandResult(int result2) {
            this.result = result2;
        }

        public CommandResult(int result2, String successMsg2, String errorMsg2) {
            this.result = result2;
            this.successMsg = successMsg2;
            this.errorMsg = errorMsg2;
        }

        public String toString() {
            return "CommandResult{result=" + this.result + ", successMsg='" + this.successMsg + '\'' + ", errorMsg='" + this.errorMsg + '\'' + '}';
        }
    }
}
