(ns property-test
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [a :refer [add delete]]))


(def gen_element gen/int)

#_ {:clj-kondo/ignore [:unresolved-symbol]}
(defspec invariant
         100
         (prop/for-all [element gen_element bag #a.Node{:value 1, :count 1, :left nil, :right nil}]
                       (let [new_bag (add bag element)]
                         (= (delete new_bag element) bag))))
