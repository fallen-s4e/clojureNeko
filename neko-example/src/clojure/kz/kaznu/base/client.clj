(ns kz.kaznu.base.client
  "Defines repl and remote repl for client"
  (:import [java.io BufferedReader IOException InputStreamReader OutputStreamWriter])
  (:import [java.net HttpURLConnection MalformedURLException ProtocolException URL URLEncoder])
  (:require clojure.string)
  (:gen-class)
  (:require kz.kaznu.base.serialization))

;; requesting http through java API

(defn request-get[ address ]
  (let [url      (URL. address)
        conn     (.openConnection url)]
    (try
      (doto conn
        (.setRequestMethod "GET")
        (.setDoOutput true)
        (.setReadTimeout 10000)
        (.connect))

      (let [inp (BufferedReader. (InputStreamReader. (.getInputStream conn)))
            sb  (StringBuilder.)
            res (loop [line (.readLine inp)]
                  (if line
                    (do (.append sb (str line "\n"))
                        (recur (.readLine inp)))
                    (.toString sb)))]
        res)
      (catch Exception ex
        (.printStackTrace ex)
        ex)
      (finally
       (.disconnect conn)))))

;; other things

(defn catch-all[ fun ]
  (fn[& xs]
    (try (apply fun xs)
         (catch Exception ex `(:error ~(str ex))))))

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