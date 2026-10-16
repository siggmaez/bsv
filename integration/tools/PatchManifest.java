import com.reandroid.apk.ApkModule;
import com.reandroid.arsc.chunk.xml.*;
import java.io.File;
import java.nio.file.*;
import java.util.Iterator;
import com.reandroid.archive.ByteInputSource;

/** Edits Android metadata only. Native libraries and original DEX stay untouched. */
public class PatchManifest {
    public static void main(String[] args) throws Exception {
        try (ApkModule module = ApkModule.loadApkFile(new File(args[0]))) {
            AndroidManifestBlock manifest = module.getAndroidManifestBlock();
            String oldPackage = manifest.getPackageName();
            String newPackage = "dev.bsv.client";
            // Expand relative component names before changing application id.
            for (Iterator<ResXmlElement> i = manifest.getManifestElement().recursiveElements(); i.hasNext();) {
                ResXmlElement e = i.next();
                for (Iterator<ResXmlAttribute> a = e.getAttributes(); a.hasNext();) {
                    ResXmlAttribute attr = a.next();
                    String value = attr.getValueAsString();
                    if (value == null) continue;
                    String name = attr.getName();
                    if ((name.equals("authorities") || name.equals("permission") || name.equals("readPermission") || name.equals("writePermission") ||
                         (name.equals("name") && (e.getName().equals("permission") || e.getName().equals("uses-permission")))) && value.startsWith(oldPackage + ".")) {
                        attr.setValueAsString(newPackage + value.substring(oldPackage.length()));
                    } else if (name.equals("name") && value.startsWith(".") && !e.getName().equals("meta-data")) {
                        attr.setValueAsString(oldPackage + value);
                    }
                }
            }
            manifest.setPackageName(newPackage);
            manifest.setApplicationLabel("BSV • Brawl Stars");
            ResXmlElement app = manifest.getApplicationElement();
            for (ResXmlElement activity : app.listElements("activity")) {
                    ResXmlAttribute name = activity.searchAttributeByName("name");
                    if (name != null && name.getValueAsString().equals("com.supercell.brawlstars.GameApp"))
                        activity.getOrCreateAndroidAttribute("label", 0x01010001).setValueAsString("BSV • Brawl Stars");
            }
            ResXmlElement provider = app.newElement("provider");
            provider.getOrCreateAndroidAttribute("name", 0x01010003).setValueAsString("dev.bsv.integration.BsvProvider");
            provider.getOrCreateAndroidAttribute("authorities", 0x01010018).setValueAsString(newPackage + ".bsv.init");
            provider.getOrCreateAndroidAttribute("exported", 0x01010010).setValueAsBoolean(false);
            manifest.refresh();
            int dexIndex = 2;
            while (module.getInputSource("classes" + dexIndex + ".dex") != null) dexIndex++;
            module.add(new ByteInputSource(Files.readAllBytes(Paths.get(args[2])), "classes" + dexIndex + ".dex"));
            module.writeApk(new File(args[1]));
        }
    }
}
