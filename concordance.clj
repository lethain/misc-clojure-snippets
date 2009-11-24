(ns read-file-examples (:use [clojure.contrib.duck-streams :only (spit read-lines)]))
(prn (reduce (fn [acc line] (reduce #(assoc %1 %2 (+ (get %1 %2 0) 1))
			       acc (.split (.toLowerCase line) " ")))
	(hash-map) (read-lines (first (rest *command-line-args*)))))
