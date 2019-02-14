package minidropbox;

public class OsUtils
{
   public static final String SLASH_WINDOWS = "\\";
   public static final String SLASH_LINUX = "/";
   private static String OS = null;
   public static String getOsName()
   {
      if(OS == null) { OS = System.getProperty("os.name"); }
      return OS;
   }
   public static boolean isWindows()
   {
      return getOsName().startsWith("Windows");
   }
   public static String getSlash() {
       if(isWindows())
           return SLASH_WINDOWS;
       else return SLASH_LINUX;
       
   }

}