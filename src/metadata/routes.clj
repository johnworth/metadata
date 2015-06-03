(ns metadata.routes
  (:use [clojure-commons.lcase-params :only [wrap-lcase-params]]
        [clojure-commons.query-params :only [wrap-query-params]]
        [compojure.api.sweet])
  (:require [metadata.routes.status :as status-routes]
            [metadata.routes.templates :as template-routes]
            [metadata.util.config :as config]
            [metadata.util.service :as service]
            [ring.middleware.keyword-params :as params]
            [schema.core :as s]
            [service-logging.thread-context :as tc]))

(defn context-middleware
  [handler]
  (tc/wrap-thread-context handler config/svc-info))

(defapi app
  (swagger-ui)
  (swagger-docs
    {:info {:title "Discovery Environment Metadata API"
            :description "Documentation for the Discovery Environment Metadata REST API"
            :version "2.0.0"}
     :tags [{:name "service-info", :description "Service Information"}
            {:name ""}]})
  (middlewares
    [tc/add-user-to-context
     wrap-query-params
     wrap-lcase-params
     params/wrap-keyword-params
     service/req-logger
     context-middleware]
    status-routes/status
    template-routes/templates))
