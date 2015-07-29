/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcomicdownloader.enums;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jcomicdownloader.tools.Common;

import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

public class EnumGenerator {

   private final static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

   private static void setFailsafeFieldValue(Field field, Object target, Object value) throws NoSuchFieldException,
           IllegalAccessException {

       // let's make the field accessible
       field.setAccessible(true);

       // next we change the modifier in the Field instance to
       // not be final anymore, thus tricking reflection into
       // letting us modify the static final field
       Field modifiersField = Field.class.getDeclaredField("modifiers");
       modifiersField.setAccessible(true);
       int modifiers = modifiersField.getInt(field);

       // blank out the final bit in the modifiers int
       modifiers &= ~Modifier.FINAL;
       modifiersField.setInt(field, modifiers);

       FieldAccessor fa = reflectionFactory.newFieldAccessor(field, false);
       fa.set(target, value);
   }

   private static void blankField(Class<?> enumClass, String fieldName) throws NoSuchFieldException,
           IllegalAccessException {
       for (Field field : Class.class.getDeclaredFields()) {
           if (field.getName().contains(fieldName)) {
               AccessibleObject.setAccessible(new Field[] { field }, true);
               setFailsafeFieldValue(field, enumClass, null);
               break;
           }
       }
   }

   private static void cleanEnumCache(Class<?> enumClass) throws NoSuchFieldException, IllegalAccessException {
       blankField(enumClass, "enumConstantDirectory"); // Sun (Oracle?!?) JDK 1.5/6
       blankField(enumClass, "enumConstants"); // IBM JDK
   }

   private static ConstructorAccessor getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes)
           throws NoSuchMethodException {
       Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
       parameterTypes[0] = String.class;
       parameterTypes[1] = int.class;
       System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
       return reflectionFactory.newConstructorAccessor(enumClass.getDeclaredConstructor(parameterTypes));
   }

   private static Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes,
           Object[] additionalValues) throws Exception {
       Object[] parms = new Object[additionalValues.length + 2];
       parms[0] = value;
       parms[1] = Integer.valueOf(ordinal);
       System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
       return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(parms));
   }

   /**
    * Add an enum instance to the enum class given as argument
    *
    * @param <T> the type of the enum (implicit)
    * @param enumType the class of the enum to be modified
    * @param enumName the name of the new enum instance to be added to the class.
    */
   @SuppressWarnings("unchecked")
   public static <T extends Enum<?>> void addSiteEnum(Class<T> enumType, String enumName,String parser) {

       // 0. Sanity checks
       if (!Enum.class.isAssignableFrom(enumType)) {
           throw new RuntimeException("class " + enumType + " is not an instance of Enum");
       }

       // 1. Lookup "$VALUES" holder in enum class and get previous enum instances
       Field valuesField = null;
       Field[] fields = enumType.getDeclaredFields();
       for (Field field : fields) {
           if (field.getName().contains("$VALUES")) {
               valuesField = field;
               break;
           }
       }
       AccessibleObject.setAccessible(new Field[] { valuesField }, true);

       try {

           // 2. Copy it
           T[] previousValues = (T[]) valuesField.get(enumType);
           List<T> values = new ArrayList<T>(Arrays.asList(previousValues));

           // 3. build new enum
           T newValue = (T) makeEnum(enumType, // The target enum class
                   enumName, // THE NEW ENUM INSTANCE TO BE DYNAMICALLY ADDED
                   values.size(),
                   new Class<?>[] {String.class}, // could be used to pass values to the enum constuctor if needed
                   new Object[] { parser }); // could be used to pass values to the enum constuctor if needed

           // 4. add new value
           values.add(newValue);

           // 5. Set new values field
           setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));

           // 6. Clean enum cache
           cleanEnumCache(enumType);

       } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException(e.getMessage(), e);
       }
   }

//   private static enum TestEnum {
//       a,
//       b,
//       c;
//   };
//
//   public static void main(String[] args) {
//
//       // Dynamically add 3 new enum instances d, e, f to TestEnum
//       addEnum(TestEnum.class, "d");
//       addEnum(TestEnum.class, "e");
//       addEnum(TestEnum.class, "f");
//
//       // Run a few tests just to show it works OK.
//       System.out.println(Arrays.deepToString(TestEnum.values()));
//       // Shows : [a, b, c, d, e, f]
//   }

    public static void addSiteEnums(){
       
        addSiteEnum(Site.class,"KUKU","ParseKUKU");
        addSiteEnum(Site.class,"EH","ParseEH");
        addSiteEnum(Site.class,"NINENINE_MANGA","Parse99Manga");
        addSiteEnum(Site.class,"NINENINE_COMIC","Parse99Comic");
        addSiteEnum(Site.class,"NINENINE_99770","Parse99770");
        addSiteEnum(Site.class,"NINENINE_COCO","ParseCoco");
        addSiteEnum(Site.class,"NINENINE_MH","Parse99Mh");
        addSiteEnum(Site.class,"NINENINE_1MH","Parse1Mh");
        addSiteEnum(Site.class,"NINENINE_3G","Parse3G");
        addSiteEnum(Site.class,"ONE_SEVEN_EIGHT","Parse178");
        addSiteEnum(Site.class,"EIGHT_COMIC","ParseEC");
        addSiteEnum(Site.class,"EIGHT_COMIC_PHOTO","ParseECphoto");
        addSiteEnum(Site.class,"JUMPCNCN","ParseJumpcncn");
        addSiteEnum(Site.class,"DMEDEN","ParseDmeden");
        addSiteEnum(Site.class,"JUMPCN","ParseJumpcn");
        addSiteEnum(Site.class,"MANGAFOX","ParseMangaFox");
        addSiteEnum(Site.class,"MANMANKAN","ParseManmankan");
        addSiteEnum(Site.class,"XINDM","ParseXindm");
        addSiteEnum(Site.class,"EX","ParseEX");
        addSiteEnum(Site.class,"WY","");
        addSiteEnum(Site.class,"GOOGLE_PIC","ParseGooglePic");
        addSiteEnum(Site.class,"BING_PIC","");
        addSiteEnum(Site.class,"BAIDU_PIC","");
        addSiteEnum(Site.class,"NANA","ParseNANA");
        addSiteEnum(Site.class,"CITY_MANGA","ParseCityManga");
        addSiteEnum(Site.class,"IIBQ","ParseIIBQ");
        addSiteEnum(Site.class,"BAIDU","ParseBAIDU");
        addSiteEnum(Site.class,"SF","ParseSF");
        addSiteEnum(Site.class,"KKKMH","ParseKKKMH");
        addSiteEnum(Site.class,"SIX_COMIC","ParseSixComic");
        addSiteEnum(Site.class,"MANHUA_178","Parse178");
        addSiteEnum(Site.class,"KANGDM","ParseKangdm");
        addSiteEnum(Site.class,"BENGOU","ParseBengou");
        addSiteEnum(Site.class,"EMLAND","ParseEmland");
        addSiteEnum(Site.class,"MOP","ParseMOP");
        addSiteEnum(Site.class,"DM5","ParseDM5");
        addSiteEnum(Site.class,"CK","ParseCK");
        addSiteEnum(Site.class,"TUKU","ParseTUKU");
        addSiteEnum(Site.class,"HH","ParseHH");
        addSiteEnum(Site.class,"IASK","ParseIASK");
        addSiteEnum(Site.class,"NINENINE_MH_99770","ParseMh99770");
        addSiteEnum(Site.class,"JM","ParseJM");
        //addSiteEnum(Site.class,"DM5_ENGLISH","");
        addSiteEnum(Site.class,"NINENINE_COMIC_TC","Parse99ComicTC");
        addSiteEnum(Site.class,"NINENINE_MANGA_TC","Parse99MangaTC");
        addSiteEnum(Site.class,"MANGA_WINDOW","ParseMangaWindow");
        addSiteEnum(Site.class,"CK_NOVEL","ParseCKNovel");
        addSiteEnum(Site.class,"MYBEST","ParseMyBest");
        addSiteEnum(Site.class,"IMANHUA","ParseImanhua");
        addSiteEnum(Site.class,"VERYIM","ParseVeryim");
        addSiteEnum(Site.class,"WENKU","ParseWenku");
        addSiteEnum(Site.class,"FUMANHUA","ParseFumanhua");
        addSiteEnum(Site.class,"SIX_MANGA","ParseSixManga");
        addSiteEnum(Site.class,"NINENINE_COCO_TC","ParseCocoTC");
        addSiteEnum(Site.class,"XXBH","ParseXXBH");
        addSiteEnum(Site.class,"COMIC_131","Parse131");
        addSiteEnum(Site.class,"BLOGSPOT","ParseBlogspot");
        addSiteEnum(Site.class,"PIXNET_BLOG","ParsePixnetBlog");
        addSiteEnum(Site.class,"XUITE_BLOG","ParseXuiteBlog");
        addSiteEnum(Site.class,"YAM_BLOG","ParseYamBlog");
        addSiteEnum(Site.class,"EYNY_NOVEL","ParseEynyNovel");
        //t addSiteEnum(Site.class,"KKMH","ParseZuiwanju");
        addSiteEnum(Site.class,"TWO_ECY","Parse2ecy");
        addSiteEnum(Site.class,"TIANYA_BOOK","ParseTianyaBook");
        addSiteEnum(Site.class,"NINENINE_MANGA_WWW","Parse99MangaWWW");
        addSiteEnum(Site.class,"EIGHT_NOVEL","ParseEightNovel");
        addSiteEnum(Site.class,"QQ_BOOK","ParseQQBook");
        addSiteEnum(Site.class,"SINA_BOOK","ParseSinaBook");
        addSiteEnum(Site.class,"FIVEONE_CTO","Parse51Cto");
        addSiteEnum(Site.class,"ONESEVEN_KK","Parse17KK");
        addSiteEnum(Site.class,"QQ_ORIGIN_BOOK","ParseQQOriginBook");
        addSiteEnum(Site.class,"UUS8","ParseUUS8");
        addSiteEnum(Site.class,"WENKU8","ParseWenku8");
        addSiteEnum(Site.class,"IFENG_BOOK","ParseIfengBook");
        addSiteEnum(Site.class,"XUNLOOK","ParseXunlook");
        addSiteEnum(Site.class,"WENKU7","WENKU7");
        addSiteEnum(Site.class,"WOYOUXIAN","WOYOUXIAN");
        addSiteEnum(Site.class,"SHUNONG","ParseShunong");
        addSiteEnum(Site.class,"SOGOU","ParseSogou");
        addSiteEnum(Site.class,"TING1","Parse1Ting");
        addSiteEnum(Site.class,"XIAMI","ParseXiami");
        addSiteEnum(Site.class,"WIKI","ParseWiki");
        addSiteEnum(Site.class,"PTT","ParsePtt");
        addSiteEnum(Site.class,"ISHUHUI","ParseIshuhui");               
        
        Common.debugPrintln(Arrays.deepToString(Site.values()));
    }
}