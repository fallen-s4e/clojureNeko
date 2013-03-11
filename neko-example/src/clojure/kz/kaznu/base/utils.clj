(ns kz.kaznu.base.utils
  "Defines repl and remote repl for client"
  (:import [java.io BufferedReader IOException InputStreamReader OutputStreamWriter])
  (:import [java.net HttpURLConnection MalformedURLException ProtocolException URL URLEncoder NetworkInterface])
  (:require clojure.string)
  (:gen-class)
  (:require kz.kaznu.base.serialization))

;; networking utils

(defn request-get
  "requesting http through java API"
  [ address ]
  (let [url      (URL. address)
        conn     (.openConnection url)]
    (try
      (doto conn
        (.setRequestMethod "GET")
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

(defn get-ip[]
  (let [ifc (NetworkInterface/getNetworkInterfaces)
        ipsq (filter #(not (.isLoopback %)) (enumeration-seq ifc))
        ipa  (map #(.getInterfaceAddresses %) ipsq)
        ipaf (nth ipa 0)
        ipafs (.split (str ipaf) " ")
        ips (first (nnext ipafs))]
    (str (second (.split ips "/")))))

;; other kind of utils

(defn catch-all[ fun ]
  (fn[& xs]
    (try (apply fun xs)
         (catch Exception ex `(:error ~(str ex))))))
