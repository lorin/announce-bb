#!/usr/bin/env bb
(ns announce
  (:require [clojure.string :refer [replace-first trim]])
  (:require [clojure.java.shell :refer [sh]]))

(def usage "Usage:

    announce on
    announce off
    announce status")

(defn expand-home [s]
  (if (.startsWith s "~")
    (replace-first s "~" (System/getProperty "user.home"))
    s))

(defn set-pref
  "Set the value in a nested key"
  [domain-name key nested-key]
  (let [domain (str (System/getProperty "home") "/Library/Preferences/" domain-name)]
    (sh "defaults" "write" domain key "-dict-add" nested-key "-bool" "YES")))


(defn unset-pref
  "Set the value in a nested key"
  [domain-name key nested-key]
  (let [domain (str (System/getProperty "home") "/Library/Preferences/" domain-name)]
    (sh "defaults" "write" domain key "-dict-add" nested-key "-bool" "NO")))

(defn set-announce-the-time-pref
  []
  (set-pref "com.apple.speech.synthesis.general.prefs" "TimeAnnouncementPrefs" "TimeAnnouncementsEnabled"))

(defn unset-announce-the-time-pref
  []
  (unset-pref "com.apple.speech.synthesis.general.prefs" "TimeAnnouncementPrefs" "TimeAnnouncementsEnabled"))


(defn start-service
  [service-name]
  (let [uid (trim (:out (sh "id" "-u")))
        service (str "gui/" uid "/" service-name)]
    (sh "launchctl" "kickstart" service)))
  

(defn start-speech-synthesis-server
  []
  (start-service "com.apple.speech.synthesisserver"))


(defn turn-on
  []
  (set-announce-the-time-pref)
  (start-speech-synthesis-server))

(defn turn-off
  []
  (unset-announce-the-time-pref)
  (println "turn it off!"))

(defn check-status
  []
  (println "check the status!"))

(defn main
  [args]
  (if-let [[arg] args]
    (case arg
      "on" (turn-on)
      "off" (turn-off)
      "status" (check-status)
      (println usage))
    (println usage)))

(main *command-line-args*)