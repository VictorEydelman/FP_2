(ns property_test
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [a :refer [create-bag add remove create-node filter_min_or_equal filter_max_or_equal map reduce_left reduce_right]]))


(def gen_element
  gen/int)

#_ {:clj-kondo/ignore [:unresolved-symbol]}
(defspec invariant
         100
         (prop/for-all [element gen_element bag #a.Node{:value 1, :count 1, :left nil, :right nil}]
                       (let [new_bag (add bag element)]
                         (= (remove new_bag element) bag))))