"""
Eureka Service Discovery Client
Handles registration and heartbeat with Eureka Server
"""
from py_eureka_client import eureka_client
from loguru import logger
from app.config.settings import settings


class EurekaService:
    """Eureka client service for service discovery registration"""
    
    def __init__(self):
        self.is_registered = False
    
    async def register(self):
        """Register this service with Eureka Server"""
        if not settings.eureka_enabled:
            logger.info("Eureka registration is disabled")
            return
        
        try:
            logger.info(f"Registering with Eureka Server at {settings.eureka_server_url}")
            logger.info(f"Service name: {settings.eureka_app_name}, Host: {settings.eureka_instance_host}, Port: {settings.eureka_instance_port}")
            
            # Build URLs for health check and status pages
            # In Docker, use container name; otherwise use hostname
            base_url = f"http://{settings.eureka_instance_host}:{settings.eureka_instance_port}"
            
            eureka_client.init(
                eureka_server=settings.eureka_server_url,
                app_name=settings.eureka_app_name,
                instance_port=settings.eureka_instance_port,
                instance_host=settings.eureka_instance_host,
                renewal_interval_in_secs=settings.eureka_lease_renewal_interval,
                duration_in_secs=settings.eureka_lease_duration,
                # Health check URL - Eureka will use this to check service health
                health_check_url=f"{base_url}/health",
                # Status page URL
                status_page_url=f"{base_url}/",
                # Home page URL
                home_page_url=f"{base_url}/",
            )
            
            self.is_registered = True
            logger.info(f"Successfully registered {settings.eureka_app_name} with Eureka Server")
            logger.info(f"Service will be available at: {base_url}")
            
        except Exception as e:
            logger.error(f"Failed to register with Eureka Server: {e}")
            logger.exception("Eureka registration error details:")
            # Don't raise exception - allow app to continue even if Eureka registration fails
            self.is_registered = False
    
    async def deregister(self):
        """Deregister this service from Eureka Server"""
        if not settings.eureka_enabled or not self.is_registered:
            return
        
        try:
            logger.info(f"Deregistering {settings.eureka_app_name} from Eureka Server")
            eureka_client.stop()
            self.is_registered = False
            logger.info("Successfully deregistered from Eureka Server")
            
        except Exception as e:
            logger.error(f"Failed to deregister from Eureka Server: {e}")

