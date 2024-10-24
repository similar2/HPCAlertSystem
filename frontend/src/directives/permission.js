import { useUserStore } from '@/stores';

export default {
  mounted(el, binding) {
    const userStore = useUserStore();
    const requiredPermission = binding.value;
    
    // 检查权限
    if (!userStore.permissions.includes(requiredPermission)) {
      el.style.display = 'none'; // 隐藏元素
      
      // 检查并隐藏父菜单
      const parentMenu = el.closest('.el-sub-menu');
      if (parentMenu) {
        const siblingItems = parentMenu.querySelectorAll('.el-menu-item');
        const visibleItems = Array.from(siblingItems).some(item => item.style.display !== 'none');
        
        if (!visibleItems) {
          parentMenu.style.display = 'none'; // 隐藏父菜单
        }
      }
    }
  }
};