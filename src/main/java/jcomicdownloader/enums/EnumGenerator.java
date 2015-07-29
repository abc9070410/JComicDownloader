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

import jcomicdownloader.module.ParseOnlineComicSite;

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
   public static <T extends Enum<?>> void addSiteEnum(Class<T> enumType, String enumName,String parser,boolean novelSite,boolean musicSite,boolean blogSite,boolean downloadBefore) {

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
                   new Class<?>[] {String.class,Boolean.class,Boolean.class,Boolean.class,Boolean.class}, // could be used to pass values to the enum constuctor if needed
                   new Object[] { parser,novelSite,musicSite,blogSite,downloadBefore }); // could be used to pass values to the enum constuctor if needed
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
        ParseOnlineComicSite s;
        
        String[] parserModules =        
            {"jcomicdownloader.module.ParseKUKU",
            "jcomicdownloader.module.ParseEH",
            "jcomicdownloader.module.Parse99Manga",
            "jcomicdownloader.module.Parse99Comic",
            "jcomicdownloader.module.Parse99770",
            "jcomicdownloader.module.ParseCocoTC",//need place before coco
            "jcomicdownloader.module.ParseCoco",
            "jcomicdownloader.module.Parse99Mh",
            "jcomicdownloader.module.Parse1Mh",
            "jcomicdownloader.module.Parse3G",
            "jcomicdownloader.module.Parse178",            
            "jcomicdownloader.module.ParseECphoto",//need place before EC
            "jcomicdownloader.module.ParseEC",
            "jcomicdownloader.module.ParseJumpcncn",
            "jcomicdownloader.module.ParseDmeden",
            "jcomicdownloader.module.ParseJumpcn",
            "jcomicdownloader.module.ParseMangaFox",
            "jcomicdownloader.module.ParseManmankan",
            "jcomicdownloader.module.ParseXindm",
            "jcomicdownloader.module.ParseEX",
            "jcomicdownloader.module.ParseGooglePic",
            "jcomicdownloader.module.ParseNANA",
            "jcomicdownloader.module.ParseCityManga",
            "jcomicdownloader.module.ParseIIBQ",
            "jcomicdownloader.module.ParseBAIDU",
            "jcomicdownloader.module.ParseSF",
            "jcomicdownloader.module.ParseKKKMH",
            "jcomicdownloader.module.ParseSixComic",
            "jcomicdownloader.module.Parse178",
            "jcomicdownloader.module.ParseKangdm",
            "jcomicdownloader.module.ParseBengou",
            "jcomicdownloader.module.ParseEmland",
            "jcomicdownloader.module.ParseMOP",
            "jcomicdownloader.module.ParseDM5",
            "jcomicdownloader.module.ParseCK",
            "jcomicdownloader.module.ParseTUKU",
//            "jcomicdownloader.module.ParseHH",
            "jcomicdownloader.module.ParseIASK",
            "jcomicdownloader.module.ParseMh99770",
            "jcomicdownloader.module.ParseJM",
            "jcomicdownloader.module.Parse99ComicTC",
            "jcomicdownloader.module.Parse99MangaTC",
            "jcomicdownloader.module.ParseMangaWindow",
            "jcomicdownloader.module.ParseCKNovel",
            "jcomicdownloader.module.ParseMyBest",
            "jcomicdownloader.module.ParseImanhua",
            "jcomicdownloader.module.ParseVeryim",
            "jcomicdownloader.module.ParseWenku",
            "jcomicdownloader.module.ParseFumanhua",
            "jcomicdownloader.module.ParseSixManga",
            "jcomicdownloader.module.ParseXXBH",
            "jcomicdownloader.module.Parse131",
            "jcomicdownloader.module.ParseBlogspot",
            "jcomicdownloader.module.ParsePixnetBlog",
            "jcomicdownloader.module.ParseXuiteBlog",
            "jcomicdownloader.module.ParseYamBlog",
            "jcomicdownloader.module.ParseEynyNovel",
            "jcomicdownloader.module.ParseZuiwanju",
            "jcomicdownloader.module.Parse2ecy",
            "jcomicdownloader.module.ParseTianyaBook",
            "jcomicdownloader.module.Parse99MangaWWW",
            "jcomicdownloader.module.ParseEightNovel",
            "jcomicdownloader.module.ParseQQBook",
            "jcomicdownloader.module.ParseSinaBook",
            "jcomicdownloader.module.Parse51Cto",
            "jcomicdownloader.module.Parse17KK",
            "jcomicdownloader.module.ParseQQOriginBook",
            "jcomicdownloader.module.ParseUUS8",
            "jcomicdownloader.module.ParseWenku8",
            "jcomicdownloader.module.ParseIfengBook",
            "jcomicdownloader.module.ParseXunlook",
            "jcomicdownloader.module.Parse7Wenku",
            "jcomicdownloader.module.ParseWoyouxian",
            "jcomicdownloader.module.ParseShunong",
            "jcomicdownloader.module.ParseSogou",
            "jcomicdownloader.module.Parse1Ting",
            "jcomicdownloader.module.ParseXiami",
            "jcomicdownloader.module.ParseWiki",
            "jcomicdownloader.module.ParsePtt",
            "jcomicdownloader.module.ParseIshuhui"};
        
        for (String name:parserModules){
            try{
                s = (ParseOnlineComicSite ) Class.forName(name).newInstance();
                addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
            }catch(Exception e){
                Common.debugPrintln("Module "+name+" load fail");
            }
        }        
        Common.debugPrint("Support Modules: ");
        Common.debugPrintln(Arrays.deepToString(Site.values()));
    }
}