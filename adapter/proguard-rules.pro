# Protect all public classes, methods and fields in this IS adapter SDK
-keep class com.ironsource.adapters.custom.tempo.** { public *; }

-repackageclasses 'com.ironsource.adapters.custom.tempo.internal'
#-classobfuscationdictionary obf_dict.txt

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable