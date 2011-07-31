(ns #^{:author "Roman Scherer"
       :doc "Rails-like inflections for Clojure.

Examples:

  (use 'inflections)
  ;=> nil

  (pluralize \"word\")
  ;=> \"words\"

  (pluralize \"virus\")
  ;=> \"viri\"

  (singularize \"apples\")
  ;=> \"apple\"

  (singularize \"octopi\")
  ;=> \"octopus\"

  (underscore \"puni-puni\")
  ;=> \"puni_puni\"

  (ordinalize \"52\")
  ;=> \"52nd\"

  (capitalize \"clojure\")
  ;=> \"Clojure\""}
  inflections.core
  (:require [inflections.transform :as t]
            [inflections.irregular :as i]
            [inflections.uncountable :as u]
            [inflections.plural :as p]
            [inflections.singular :as s])
  (:use [clojure.walk :only (postwalk)]))

(defn camelize
  "Convert obj to camel case. By default, camelize converts to
  UpperCamelCase. If the argument to camelize is set to :lower then
  camelize produces lowerCamelCase. The camelize fn will also convert
  \"/\" to \"::\" which is useful for converting paths to namespaces.

  Examples:

    (camelize \"active_record\")
    ;=> \"ActiveRecord\"

    (camelize \"active_record\" :lower)
    ;=> \"activeRecord\"

    (camelize \"active_record/errors\")
    ;=> \"ActiveRecord::Errors\"

    (camelize \"active_record/errors\" :lower)
    ;=> \"activeRecord::Errors\""
  [obj & [mode]] (t/camelize obj mode))

(defn capitalize
  "Convert the first letter in obj to upper case.

  Examples:

    (capitalize \"hello\")
    ;=> \"Hello\"

    (capitalize \"HELLO\")
    ;=> \"Hello\"

    (capitalize \"abc123\")
    ;=> \"Abc123\""
  [obj] (t/capitalize obj))

(defn dasherize
  "Replaces all underscores in obj with dashes.

  Examples:

    (dasherize \"puni_puni\")
    ;=> \"puni-puni\""
  [obj] (t/dasherize obj))

(defn demodulize
  "Removes the module part from obj.

  Examples:

    (demodulize \"inflections.MyRecord\")
    ;=> \"MyRecord\"

    (demodulize \"ActiveRecord::CoreExtensions::String::Inflections\")
    ;=> \"Inflections\"

    (demodulize \"Inflections\")
    ;=> \"Inflections\""
  [obj] (t/demodulize obj))

(defn foreign-key
  "Converts obj into a foreign key. The default separator \"_\" is
  placed between the name and \"id\".


  Examples:

    (foreign-key \"Message\")
    ;=> \"message_id\"

    (foreign-key \"Message\" false)
    ;=> \"messageid\"

    (foreign-key \"Admin::Post\")
    ;=> \"post_id\""
  [obj & [separator]] (t/foreign-key obj separator))

(defn hyphenize
  "Hyphenize obj, which is the same as threading obj through the str,
  underscore and dasherize fns.

  Examples:

    (hyphenize 'Continent)
    ; => \"continent\"

    (hyphenize \"CountryFlag\")
    ; => \"country-flag\""
  [obj] (t/hyphenize obj))

(defn irregular?
  "Returns true if obj is a irregular word, otherwise false

  Examples:

    (irregular? \"child\")
    ;=> true

    (irregular? \"word\")
    ;=> false"
  [obj] (i/irregular? obj))

(defn ordinalize
  "Turns obj into an ordinal string used to denote the position in an
  ordered sequence such as 1st, 2nd, 3rd, 4th, etc.

  Examples:

    (ordinalize \"1\")
    ;=> \"1st\"

    (ordinalize \"23\")
    ;=> \"23rd\""
  [obj] (t/ordinalize obj))

(defn parameterize
  "Replaces special characters in obj with the default separator
  \"-\". so that it may be used as part of a pretty URL.

  Examples:

    (parameterize \"Donald E. Knuth\")
    ; => \"donald-e-knuth\"

    (parameterize \"Donald E. Knuth\" \"_\")
    ; => \"donald_e_knuth\""
  [obj & [separator]] (t/parameterize obj separator))

(defn pluralize
  "Returns the plural of obj.

  Example:

    (pluralize \"virus\")
    ; => \"virii\""
  [obj] (p/plural obj))

(defn singularize
  "Returns the singular of obj.

  Example:

    (singularize \"mice\")
    ;=> \"mouse\""
  [obj] (s/singular obj))

(defn underscore
  "The reverse of camelize. Makes an underscored, lowercase form from
  the expression in the string. Changes \"::\" to \"/\" to convert
  namespaces to paths.

  Examples:

    (underscore \"ActiveRecord\")
    ;=> \"active_record\"

    (underscore \"ActiveRecord::Errors\")
    ;=> \"active_record/errors\""
  [obj] (t/underscore obj))

(defn underscore-keys
  "Recursively replaces all dashes with underscore of all keys in m."
  [m] (let [f (fn [[k v]]
                [(underscore k)
                 (if (map? v) (underscore-keys v) v)])]
        (postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn uncountable?
  "Returns true if obj is a uncountable word, otherwise false.

  Examples:

    (uncountable? \"alcohol\")
    ;=> true

    (uncountable? \"word\")
    ;=> false"
  [obj] (u/uncountable? obj))

(defn init-inflections []
  (p/init-plural-rules)
  (s/init-singular-rules)
  (u/init-uncountable-words)
  (i/init-irregular-words))

(init-inflections)
