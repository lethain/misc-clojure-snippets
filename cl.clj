(ns cl
  (:use [clojure.contrib.duck-streams :only (append-spit slurp*)]))

(defn get-post-urls [url]                                                                                                                                                                                  
  (map #(second %1) 
       (re-seq #"<p><a href=\"([a-zA-Z0-9/.]+)\">.*?</p>" (slurp* url))))

(defn extract-post-data [url]
  (defn strip-html-tags [string]
    (.replaceAll (.replaceAll string "<.*?>" " ") "[\t\n\r ]+" " "))
  (let [html (.replaceAll (slurp* url) "[\t\n\r]+" " ")]
    (list url
	  (second (re-find #"<h2>(.*?)</h2>" html))
	  (strip-html-tags (second (re-find #"<div id=\"userbody\">(.*?)</div>" html))))))

(defn tokenize [str]
  (reduce #(assoc %1 %2 (+ (get %1 %2 0) 1))
	  (hash-map) (.split (.toLowerCase str) " ")))

(defn has-keys? [key-map keys]
  (loop [keys-to-check keys]
    (cond (empty? keys-to-check) true
	  (contains? key-map (first keys-to-check)) (recur (rest keys-to-check))
	  :else false)))

(defn filter-post? [filter post] 
  (let [url (nth post 0)
	title (nth post 1)
	body (nth post 2)]
    (or (has-keys? (tokenize title) filter)
	(has-keys? (tokenize body) filter))))

(defn make-filter [name tags]
  {:tags tags :file (agent (str name ".posts"))})

(defn save-post [filter post]
  (defn write-to-agent [filename post]
    (append-spit filename 
		 (apply str (map #(str % "\n") post)))
    filename)
  (send (:file filter) write-to-agent
	(reverse (cons "\n\n" (reverse post)))))

(defn make-agent-pool [pool-name n]
  (doall (map #(agent (str pool-name "." %)) (range 0 n))))

(defn agent-from-pool [pool]
  (nth pool (rand-int (count pool))))

(def category-pool (make-agent-pool "category" 2))
(def post-pool (make-agent-pool "post" 5))
(def post-queue (agent (list)))

(defn retrieve-categories [categories]
  (defn add-to-post-queue [q post]
    (cons post q))
  (defn process-post [_ post-url]
    (send post-queue add-to-post-queue
	  (. java.lang.Thread sleep (rand 10000))
	  (extract-post-data post-url)))
  (defn process-category [_ category-url]
    (. java.lang.Thread sleep (rand 10000))
    (doseq [url (get-post-urls category-url)]
      (send (agent-from-pool post-pool) process-post url)))
  (doseq [category-url categories]
    (send (agent-from-pool category-pool) process-category category-url)))

(prn retrieve-categories '("http://sfbay.craigslist.org/eng/")
(. java.lang.Thread sleep 20000)
(prn @post-queue)
