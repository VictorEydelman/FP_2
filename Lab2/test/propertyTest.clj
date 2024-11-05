(ns propertyTest

  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [a :refer [add delete create-bag merger]])
  (:import (java.util Date)))

(def gen_element
  gen/int)

(def gen_bag
  (gen/vector gen_element))

(defspec invariant
  100
  (prop/for-all [element gen_element node gen_bag]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (delete (add bag element) element) bag))
    ))

(defspec merge-empty-bag
  100
  (prop/for-all [node gen_bag]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (merger bag (create-bag)) bag))
    ))
(defspec monoid
  100
  (prop/for-all [node gen_bag]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (merger (merger bag bag) bag)
         (merger bag (merger bag bag))))
    ))
(defspec polymorphic
  100
  (prop/for-all [element (gen/one-of [gen/int gen/char gen/boolean gen/string gen/double
                                      gen/sample Date]) node (gen/vector (gen/one-of [gen/int gen/char gen/boolean gen/string gen/double
                                      gen/sample Date]))]
    (let [bag (if (= node []) (create-bag) (reduce add (create-bag) node))]
      (= (delete (add bag element) element) bag))
    ))
