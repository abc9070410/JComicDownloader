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
        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseKUKU").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}
        
        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseEH").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse99Manga").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse99Comic").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse99770").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseCoco").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse99Mh").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse1Mh").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse3G").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse178").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseEC").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseECphoto").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseJumpcncn").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseDmeden").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseJumpcn").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseMangaFox").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseManmankan").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseXindm").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseEX").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseGooglePic").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseNANA").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseCityManga").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseIIBQ").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseBAIDU").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseSF").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseKKKMH").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseSixComic").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse178").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseKangdm").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseBengou").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseEmland").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseMOP").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseDM5").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseCK").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseTUKU").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseHH").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseIASK").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseMh99770").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseJM").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse99ComicTC").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse99MangaTC").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseMangaWindow").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseCKNovel").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseMyBest").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseImanhua").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseVeryim").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseWenku").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseFumanhua").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseSixManga").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseCocoTC").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseXXBH").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse131").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseBlogspot").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParsePixnetBlog").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseXuiteBlog").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseYamBlog").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseEynyNovel").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseZuiwanju").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse2ecy").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseTianyaBook").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse99MangaWWW").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseEightNovel").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseQQBook").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseSinaBook").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse51Cto").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse17KK").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseQQOriginBook").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseUUS8").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseWenku8").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseIfengBook").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseXunlook").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseWENKU7").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseWOYOUXIAN").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseShunong").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseSogou").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.Parse1Ting").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseXiami").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseWiki").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParsePtt").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        try{
            s = (ParseOnlineComicSite ) Class.forName("jcomicdownloader.module.ParseIshuhui").newInstance();
            addSiteEnum(Site.class,s.getEnumName(),s.getParserName(),s.isNovelSite(),s.isMusicSite(),s.isBlogSite(),s.isDownloadBefore());
        }catch(Exception e){}

        Common.debugPrint("Support Enum: ");
        Common.debugPrintln(Arrays.deepToString(Site.values()));
    }
}