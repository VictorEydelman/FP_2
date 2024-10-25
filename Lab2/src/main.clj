(ns main)
(defn create-bag [] {})
(defn insert
  ([bag element]
   (if (zero? (count bag))
     (insert bag element (Math/floor (/ (count bag) 2)) 0 (count bag))
     (conj bag element)))
  ([bag element now start end] (if ())))
