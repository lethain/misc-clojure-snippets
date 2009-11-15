(ns my-ns 
  (:import (java.io BufferedReader FileReader)))

(defn process-line [acc line]
  (println line)
  (println acc)
  (+ 1 (int acc)))

(defn process-lines [file-name]
  (with-open [rdr (BufferedReader. (FileReader. file-name))]
					; (reduce (fn [n y] (+ n (:fred y))) 0 x)
    (reduce process-line (int 0) (line-seq rdr))))

(process-lines "tokenize.clj")




