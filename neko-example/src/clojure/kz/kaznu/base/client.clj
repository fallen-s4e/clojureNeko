(ns kz.kaznu.base.client
  "Defines repl and remote repl for client"
  (:import [java.io BufferedReader IOException InputStreamReader OutputStreamWriter])
  (:import [java.net HttpURLConnection MalformedURLException ProtocolException URL URLEncoder NetworkInterface])
  (:require clojure.string)
  (:use kz.kaznu.base.utils)
  (:gen-class)
  (:require kz.kaznu.base.serialization))

(defn runr[addr expr]
  (let [expr-str     (URLEncoder/encode (kz.kaznu.base.serialization/seri expr) "UTF-8")
        encoded-dots (clojure.string/replace expr-str "." "%2E")
        request-str  (str addr "/repl/" encoded-dots)]
    (read-string (request-get request-str))))

(defn prompt-read[ x ]
  (print (str x ">"))
  (flush)
  (read))

(defn- repl-gen[eval-fn prompt]
  (println "Type :exit :quit or :q to exit")
  (loop [r (prompt-read prompt)]
    (if (some #{:q :quit :exit} [r])
      "exiting"
      (do (println (eval-fn r))
          (recur (prompt-read prompt))))))

(defn replr
  "starts a remote repl that works remotely on addr  which has default value = http://localhost:3000"
  [& [addr]]
  (println "Remote read eval print loop(remote REPL)")
  (repl-gen (catch-all #(runr (or addr "http://localhost:3000") %))
            "Remote"))

(defn replr-local
  "starts a remote repl that works remotely on localhost which has default value = 3000"
  [& [port]]
  (println "Remote read eval print loop(remote REPL on localhost)")
  (repl-gen (catch-all #(runr (str "http://localhost:"
                                   (or port 3000)) %))
            "RemoteLH"))

(defn repl[]
  (print "Read eval print loop(REPL)")
  (repl-gen (catch-all eval) "Repl"))