(ns agents-queue)

(def logger (agent (list)))
(defn log [msg]
  (send logger #(cons %2 %1) msg))

(defn create-relay [n]
     (defn next-agent [previous _] (agent previous))
     (reduce next-agent nil (range 0 n)))

(defn relay [relay msg]
  (defn relay-msg [next-actor hop msg]
    (cond (nil? next-actor)  (log "finished relay")
	  :else (do (log (list hop msg))
		    (send next-actor relay-msg (+ hop 1) msg))))
  (send relay relay-msg 0 msg))

(relay (create-relay 10) "hello")
(. java.lang.Thread sleep 5000)

(prn @logger)