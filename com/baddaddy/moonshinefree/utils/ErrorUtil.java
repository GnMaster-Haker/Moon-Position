package com.baddaddy.moonshinefree.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import com.baddaddy.moonshinefree.C0012R;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ErrorUtil {

    /* renamed from: com.baddaddy.moonshinefree.utils.ErrorUtil.1 */
    class C00131 implements OnClickListener {
        private final /* synthetic */ Context val$context;
        private final /* synthetic */ Exception val$e;

        C00131(Context context, Exception exception) {
            this.val$context = context;
            this.val$e = exception;
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            ErrorUtil.sendError(this.val$context, this.val$e);
        }
    }

    /* renamed from: com.baddaddy.moonshinefree.utils.ErrorUtil.2 */
    class C00142 implements OnClickListener {
        C00142() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    }

    public static void reportError(Context context, Exception e) {
        new Builder(context).setMessage(context.getString(C0012R.string.error_alert)).setCancelable(false).setPositiveButton("Yes", new C00131(context, e)).setNegativeButton("No", new C00142()).show();
    }

    public static void sendError(Context context, Exception e) {
        String str = "\n\n";
        String str2 = "\n";
        String email = context.getText(C0012R.string.email).toString();
        String subject = context.getText(C0012R.string.contact_subject).toString();
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String str3 = "\n";
        sb.append("---- Application Info  ----").append(str2);
        str3 = "\n";
        sb.append("  Name: ").append(context.getText(C0012R.string.app_name)).append(str2);
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 128);
            sb.append("  Version Num: ").append(pi.versionName).append("\n");
            sb.append("  Version Code: ").append(pi.versionCode).append("\n");
        } catch (Exception e2) {
            Exception ex = e2;
            str3 = "\n";
            sb.append("  Version Info: unknown").append(str2);
        }
        String str4 = "\n";
        sb.append(str2);
        str3 = "\n\n";
        sb.append("** Error information **").append(str);
        str3 = "\n\n";
        sb.append("  Message: ").append(e.getMessage()).append(str);
        str3 = "\n";
        sb.append("  Stacktrace: ").append(writer.toString()).append(str2);
        sendEmail(context, email, subject, sb.toString());
    }

    protected static void sendEmail(Context context, String email, String subject, String text) {
        Intent emailIntent = new Intent("android.intent.action.SEND");
        String[] emails = new String[1];
        emailIntent.setType("plain/text");
        if (email != null && email.length() > 0) {
            emails[0] = email;
            emailIntent.putExtra("android.intent.extra.EMAIL", emails);
        }
        emailIntent.putExtra("android.intent.extra.SUBJECT", subject);
        emailIntent.putExtra("android.intent.extra.TEXT", text);
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
